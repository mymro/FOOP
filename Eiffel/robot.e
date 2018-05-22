note
	description: "Summary description for {ROBOT}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	ROBOT

inherit
	GAME_OBJECT
		redefine
			draw,
			update
		end

create
	make_robot

feature {NONE}
	pos_in_labyrinth: VECTOR_2
	labyrinth: MAIN_LABYRINTH
	f_modifier: F_MODIFIER_CONTAINER


feature {NONE}
	make_robot(a_game: GAME; a_pos_in_labyrinth: VECTOR_2; a_labyrinth:MAIN_LABYRINTH)
		require
			a_pos_in_labyrinth.x > 0 and a_pos_in_labyrinth.x <= a_labyrinth.get_labyrinth_dim_x
			a_pos_in_labyrinth.y > 0 and a_pos_in_labyrinth.y <= a_labyrinth.get_labyrinth_dim_y
		local
			a_pos: VECTOR_2
		do
			labyrinth:=a_labyrinth
			create f_modifier
			pos_in_labyrinth:=a_pos_in_labyrinth
			create a_pos.make_with_xy ((pos_in_labyrinth.x-1)*labyrinth.step_width, (pos_in_labyrinth.y-1)*labyrinth.step_height)
			make(a_game, a_pos, create{VECTOR_2}.make_with_xy (labyrinth.step_width, labyrinth.step_height), 0, 2)
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

			mask.set_foreground_color_rgb (0,0,0)
			mask.draw_rectangle (0, 0, dimension.x, dimension.y)
			mask.set_foreground_color_rgb (1, 1, 1)
			mask.draw_triangle (x_center, y_center, dimension.y)

			buffer.set_foreground_color_rgb (0, 1, 0)
			buffer.draw_triangle (x_center, y_center, dimension.y)
			game.set_mask (index_buffer, index_mask)
		end
feature {ANY}

	reset_buffer
	-- redraws the buffer
		do
			if attached game.get_buffer (buffer_indices[1]) as buffer and
			attached game.get_buffer (buffer_indices[2]) as mask then
				draw_buffer(buffer, mask, buffer_indices[1], buffer_indices[2])
			else
				print("buffer not attached in main labyrinth")
			end
		end

	draw
	-- draw to display
		do
			draw_buffer_at_index(1)
			PRECURSOR{GAME_OBJECT}
		end

	update
		do
			print("update ROBOT%N")
			PRECURSOR{GAME_OBJECT}
		end
end
