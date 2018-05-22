note
	description: "Summary description for {FLAG}."
	author: "COnstantin BUdin"
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
	labyrinth_pos_x: INTEGER
	labyrinth_pos_y: INTEGER

feature {NONE}
	make(a_game: GAME; a_layer:INTEGER; some_buffer_indices: ARRAY[INTEGER]; a_color:EV_COLOR; a_labyrinth:MAIN_LABYRINTH; a_labyrinth_pos_x, a_labyrinth_pos_y:INTEGER)
		require
			some_buffer_indices.count > 0
			a_labyrinth_pos_x > 0 and a_labyrinth_pos_x <= a_labyrinth.get_labyrinth_dim_x
			a_labyrinth_pos_y > 0 and a_labyrinth_pos_y <= a_labyrinth.get_labyrinth_dim_y
		local
			a_pos: VECTOR_2
		do
			create a_pos.make_with_xy (a_labyrinth_pos_x * a_labyrinth.step_width, a_labyrinth_pos_y * a_labyrinth.step_height)
			game_object_make(a_game, a_pos, a_layer, some_buffer_indices)
			color:=a_color
			labyrinth:= a_labyrinth
			labyrinth_pos_x:= a_labyrinth_pos_x
			labyrinth_pos_y:= a_labyrinth_pos_y
			create modifier_container
			reset_buffer

			modifier_container.add_modifier(current)
			set_parent(a_labyrinth)
		end

	draw_buffer(buffer: separate EV_PIXMAP_ADVANCED)
		require
			buffer.height = dimension.y
			buffer.width = dimension.x
		do
			buffer.set_foreground_color_rgb (color.red, color.green, color.blue)
			buffer.fill_rectangle (0, 0, dimension.x, dimension.y)
		end

feature {ANY}

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
			print("update FLAG%N")
			PRECURSOR{GAME_OBJECT}
		end

end
