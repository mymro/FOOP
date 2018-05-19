note
	description: "Summary description for {FLAG}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	FLAG

inherit
	GAME_OBJECT
		rename
			make as game_object_make
		redefine
			draw
		end

create
	make

feature {NONE}
	make(a_game: GAME; a_pos: VECTOR_2; a_layer:INTEGER; some_buffer_indices: ARRAY[INTEGER])
		require
			some_buffer_indices.count > 0
		do
			game_object_make(a_game, a_pos, a_layer, some_buffer_indices)
		end

feature {ANY}
	draw
		do
			draw_buffer_at_index(1)
			PRECURSOR{GAME_OBJECT}
		end

end
