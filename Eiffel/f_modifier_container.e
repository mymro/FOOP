note
	description: "a class containing all f_modifers, in a shared list"
	author: "Constantin Budin"
	date: "22.05.2018"
	revision: "1.0"

class
	F_MODIFIER_CONTAINER

inherit
	F_MODIFIER
		undefine
			default_create
		end
	ANY
		redefine
			default_create
		end

create
	default_create

feature {NONE}
	modifiers: ARRAYED_LIST[F_MODIFIER]

feature {NONE}
	default_create
		do
			modifiers:=create_modifers_array
		end

	create_modifers_array: ARRAYED_LIST[F_MODIFIER]
		once
			create RESULT.make(0)
		end

feature {ANY}
	get_modifier_at(x,y:INTEGER):REAL_64
		local
			sum:REAL_64
		do
			sum:= 0
			across modifiers.new_cursor as cursor
			loop
				sum:= sum + cursor.item.get_modifier_at (x, y)
			end
			RESULT:= sum
		end

	add_modifier(modifier:F_MODIFIER)
		do
			if modifiers.occurrences (modifier) = 0 then
				modifiers.extend (modifier)
			end
		end

	remove_modifier(modifier:F_MODIFIER)
		do
			modifiers.prune (modifier)
		end
end
