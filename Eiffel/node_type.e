note
	description: "Node Types"
	author: "Constantin Budin"
	date: "20.05.2018"
	revision: "0.9"

class
	NODE_TYPE

inherit
	NODE_TYPE_BASE
		redefine
			default_create,
			is_equal
		end

create
	make,
	default_create

feature {ANY}
	type:INTEGER assign set_type

feature {ANY}
	default_create
		do
			make(type_unknown)
		end

	make(a_type: INTEGER)
		require
			is_type_valid(a_type)
		do
			type:= a_type
		ensure
			type = a_type
		end

	set_type(new_type: INTEGER)
		require
			is_type_valid(new_type)
		do
			type:= new_type
		ensure
			type = new_type
		end

	is_of_type(a_type:INTEGER):BOOLEAN
		do
			RESULT:= a_type = type
		end

	is_equal (other: NODE_TYPE): BOOLEAN
		do
			RESULT:= type = other.type
		end
end
