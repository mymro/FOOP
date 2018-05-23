note
	description: "A node in the labyrinth. has neighbours, type and position"
	author: "Constantin Budin"
	date: "17.05.2018"
	revision: "0.1"

class
	LABYRINTH_NODE

inherit
	HASHABLE
		undefine
			default_create
		redefine
			is_hashable,
			hash_code
		end

create
	make,
	default_create

feature {ANY}
	x: INTEGER
	y: INTEGER
	type: NODE_TYPE
	neighbours: HASH_TABLE[detachable LABYRINTH_NODE, STRING]

feature {NONE}

	default_create
		do
			make(1,1, (create {NODE_TYPE_BASE}).type_unknown)
		end

	make(a_x, a_y, a_type:INTEGER)
		require
			a_x >= 1
			a_y >= 1
		do
			set_pos(a_x, a_y)
			create type.make(a_type)
			create neighbours.make (4)
		ensure
			type.is_of_type (a_type)
			x = a_x
			y = a_y
		end


feature {ANY}

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

	set_type_integer(a_type:INTEGER)
		require
			type.is_type_valid (a_type)
		do
			type.set_type (a_type)
		end

	set_type(a_type: NODE_TYPE)
		do
			type.set_type (a_type.type)
		end

	is_of_type(a_type: INTEGER):BOOLEAN
		do
			RESULT:=type.is_of_type (a_type)
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
