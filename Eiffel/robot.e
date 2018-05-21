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
	make_robot(a_game: GAME; a_pos_relative_to_parent: VECTOR_2; a_layer:INTEGER; some_buffer_indices: ARRAY[INTEGER])
		require
			some_buffer_indices.count>1
		do
			make(a_game, a_pos_relative_to_parent, a_layer, some_buffer_indices)
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
