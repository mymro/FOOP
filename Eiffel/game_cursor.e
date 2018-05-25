note
	description: "A cursor for placing flags in labyrinth"
	author: "Constantin Budin"
	date: "25.05.2018"
	revision: "1.0"

class
	PLAYER

inherit
	GAME_OBJECT
		rename
			make as make_game_object
		redefine
			draw,
			update
		end

create
	make

feature{NONE}
	labyrinth: MAIN_LABYRINTH
	position_in_labyrinth: VECTOR_2
	keybindings: KEY_BINDINGS
	current_delta_time:REAL_64
	blink_interval: REAL_64 = 0.3
	draw_cursor: BOOLEAN

feature{NONE}
	make(a_game: GAME; a_labyrinth: MAIN_LABYRINTH; a_position_in_labyrinth: VECTOR_2; some_keybindings: KEY_BINDINGS)
		require
			a_position_in_labyrinth.x > 0 and a_position_in_labyrinth.x <= a_labyrinth.get_labyrinth_dim_x
			a_position_in_labyrinth.y > 0 and a_position_in_labyrinth.y <= a_labyrinth.get_labyrinth_dim_y
		local
			a_dimension: VECTOR_2
			a_pos: VECTOR_2
		do
			labyrinth:=a_labyrinth
			position_in_labyrinth:=a_position_in_labyrinth
			keybindings:=some_keybindings
			current_delta_time:= 0
			draw_cursor:= TRUE

			create a_dimension.make_with_xy (labyrinth.step_width, labyrinth.step_height)
			create a_pos.make_with_xy ((position_in_labyrinth.x - 1) * labyrinth.step_width, (position_in_labyrinth.y - 1) * labyrinth.step_height)

			make_game_object(a_game, a_pos, a_dimension, 2, 2)
			reset_buffer
		end

	draw_buffer(buffer, mask: separate EV_PIXMAP_ADVANCED; index_buffer, index_mask:INTEGER)
		require
			buffer.height = dimension.y
			buffer.width = dimension.x
			buffer.height = mask.height
			buffer.width = mask.width
		local
			x_center: INTEGER
			y_center: INTEGER
		do
			x_center:= (dimension.x/2).truncated_to_integer
			y_center:= (dimension.y/2).truncated_to_integer

			mask.set_foreground_color_grey(0)
			mask.fill_rectangle (0, 0, dimension.x, dimension.y)
			mask.set_foreground_color_grey (1)
			mask.set_line_width (2)
			mask.draw_straight_line (0, y_center, dimension.x, y_center)
			mask.draw_straight_line (x_center, 0, x_center, dimension.y)
			mask.draw_ellipse (1, 1, dimension.x-2, dimension.y-2)

			buffer.set_foreground_color_grey (0)
			buffer.fill_rectangle (0, 0, dimension.x, dimension.y)
			game.set_mask (index_buffer, index_mask)
		end

	recalculate_position
		do
			pos_relative_to_parent.x:= (position_in_labyrinth.x-1) * labyrinth.step_width
			pos_relative_to_parent.y:= (position_in_labyrinth.y-1) * labyrinth.step_height
		end

feature {ANY}

	reset_buffer
	-- redraws the buffer
		do
			if attached game.get_buffer (buffer_indices[1]) as buffer and
			attached game.get_buffer (buffer_indices[2]) as mask then
				draw_buffer(buffer, mask, buffer_indices[1], buffer_indices[2])
			else
				print("buffer not attached in cursor")
			end
		end

	draw
		do
			PRECURSOR{GAME_OBJECT}
			if draw_cursor then
				draw_buffer_at_index(1)
			end
		end

	update
		local
			repell_flag: FLAG_DONT_COME_NEAR
			attract_flag: FLAG_SEARCH_HERE
		do
			current_delta_time:= current_delta_time + game.delta_time
			if current_delta_time >= blink_interval then
				current_delta_time:= 0
				draw_cursor:= not draw_cursor
			end

			if game.was_new_key_pressed then

				if game.last_pressed_key = keybindings.up_code then
					if position_in_labyrinth.y > 1 then
						position_in_labyrinth.y := position_in_labyrinth.y - 1
						recalculate_position
					end
				elseif game.last_pressed_key = keybindings.down_code then
					if position_in_labyrinth.y < labyrinth.get_labyrinth_dim_y then
						position_in_labyrinth.y := position_in_labyrinth.y + 1
						recalculate_position
					end
				elseif game.last_pressed_key = keybindings.left_code then
					if position_in_labyrinth.x > 1 then
						position_in_labyrinth.x := position_in_labyrinth.x - 1
						recalculate_position
					end
				elseif game.last_pressed_key = keybindings.right_code then
					if position_in_labyrinth.x < labyrinth.get_labyrinth_dim_x then
						position_in_labyrinth.x := position_in_labyrinth.x + 1
						recalculate_position
					end
				elseif game.last_pressed_key = keybindings.place_attract_flag_code then
					create attract_flag.make_flag (game, create{VECTOR_2}.make_with_xy (position_in_labyrinth.x, position_in_labyrinth.y), labyrinth)
					if attached parent as my_parent then
						my_parent.add_child (attract_flag)
					end
				elseif game.last_pressed_key = keybindings.place_repell_flag_code then
					create repell_flag.make_flag (game, create{VECTOR_2}.make_with_xy (position_in_labyrinth.x, position_in_labyrinth.y), labyrinth)
					if attached parent as my_parent then
						my_parent.add_child (repell_flag)
					end
				end
			end

			PRECURSOR{GAME_OBJECT}
		end
end
