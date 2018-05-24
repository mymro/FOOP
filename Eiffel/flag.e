note
	description: "Summary description for {FLAG}."
	author: "Constantin Budin"
	date: "22.05.2018"
	revision: "1.0"

deferred class
	FLAG

inherit
	GAME_OBJECT
		rename
			make as game_object_make
		redefine
			draw,
			update
		end

	F_MODIFIER

feature {NONE}
	color: EV_COLOR
	modifier_container: F_MODIFIER_CONTAINER
	labyrinth: MAIN_LABYRINTH
	pos_in_labyrinth: VECTOR_2

feature {NONE}
	make(a_game: GAME; a_pos_in_labyrinth: VECTOR_2; a_color:EV_COLOR; a_labyrinth:MAIN_LABYRINTH)
		require
			a_pos_in_labyrinth.x > 0 and a_pos_in_labyrinth.x <= a_labyrinth.get_labyrinth_dim_x
			a_pos_in_labyrinth.y > 0 and a_pos_in_labyrinth.y <= a_labyrinth.get_labyrinth_dim_y
		local
			a_pos: VECTOR_2
		do
			pos_in_labyrinth:=a_pos_in_labyrinth
			color:=a_color
			labyrinth:= a_labyrinth
			create a_pos.make_with_xy ((pos_in_labyrinth.x-1) * labyrinth.step_width, (pos_in_labyrinth.y-1) * labyrinth.step_height)
			game_object_make(a_game, a_pos, create{VECTOR_2}.make_with_xy (labyrinth.step_width, labyrinth.step_height), 1, 1)
			create modifier_container
			reset_buffer

			modifier_container.add_modifier(current)
		end

	draw_buffer(buffer: separate EV_PIXMAP_ADVANCED)
		require
			buffer.height = dimension.y
			buffer.width = dimension.x
		do
			buffer.set_foreground_color_rgb (color.red, color.green, color.blue)
			buffer.fill_rectangle (0, 0, buffer.width, buffer.height)
		end

feature {ANY}

	get_modifier_at(x,y:INTEGER):REAL_64
		deferred
		end

	reset_buffer
	-- redraws the buffer
		do
			if attached game.get_buffer (buffer_indices[1]) as buffer then
				draw_buffer(buffer)
			else
				print("buffer not attached in main labyrinth")
			end
		end

	draw
		do
			draw_buffer_at_index(1)
			PRECURSOR{GAME_OBJECT}
		end

	update
		do
			PRECURSOR{GAME_OBJECT}
		end

end
