note
	description: "A node in the labyrinth. has neighbours, type and position"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"

class
	LABYRINTH_NODE

inherit
	HASHABLE
		redefine
			is_hashable,
			hash_code
		end

create
	make

feature {ANY}
	x: INTEGER
	y: INTEGER
	--0 finish
	--1 normal
	--2 unknown
	type: INTEGER assign set_type
	neighbours: HASH_TABLE[detachable LABYRINTH_NODE, STRING]

feature {NONE}

	make(a_x, a_y, a_type:INTEGER)
		require
			is_type_valid(a_type)
		do
			set_pos(a_x, a_y)
			type := a_type
			create neighbours.make (4)
		ensure
			type = a_type
		end


feature {ANY}

	set_type(new_type: INTEGER)
	--changes the type of the node
	--0 finish
	--1 normal
	--2 unknown
	require
		is_type_valid(new_type)
	do
		type:= new_type
	ensure
		type = new_type
	end

	is_type_valid(a_type:INTEGER):BOOLEAN
	-- checks if type is valid
		do
			RESULT:= a_type >=0 and a_type <=2
		end

	set_pos(a_x, a_y:INTEGER)
	-- changes the position
		require
			a_x >= 1
			a_y >= 1
		do
			x := a_x
			y := a_y
		ensure
			x = a_x
			y = a_y
		end

feature {ANY}
	is_hashable:BOOLEAN
		do
			RESULT:= TRUE
		end

	hash_code:INTEGER_32
		do
			if x >= y then
				RESULT:= x*x+x+y
			else
				RESULT:=y*y+x
			end
		end
end
