note
	description: "a 2 dimensional vector"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"

class
	VECTOR_2
inherit
	ANY
		redefine
			default_create
		end

create
	default_create,
	make_with_pos

feature{ANY}
	x: INTEGER
	y: INTEGER

feature {NONE}
	default_create
	--creates vector_2 with 1,1
		do
			x:=1
			y:=1
		ensure then
			x = 1
			y = 1
		end

	make_with_pos(a_x, a_y:INTEGER)
		do
			x := a_x
			y := a_y
		ensure
			x = a_x
			y = a_y
		end

end
