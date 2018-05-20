note
	description: "base class for node_type. Contains the consts and a mehtod to check if a type is valid"
	author: "Constantin Budin"
	date: "20.05.2018"
	revision: "1.0"

class
	NODE_TYPE_BASE

feature {ANY}
	type_finish: INTEGER = 0
	type_normal: INTEGER = 1
	type_unknown: INTEGER = 2

	is_type_valid(a_type:INTEGER):BOOLEAN
	-- checks if type is valid
		do
			RESULT:= a_type = type_finish or
					a_type = type_normal or
					a_type = type_unknown
		end
end
