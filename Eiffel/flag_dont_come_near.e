note
	description: "A flag that repels"
	author: "COnstantin BUdin"
	date: "22.05.2018"
	revision: "1.0"

class
	FLAG_DONT_COME_NEAR

inherit
	FLAG

create
	make

feature {ANY}

	get_modifier_at(x,y:INTEGER):REAL_64
		local
			e:REAL_64
			abs_pos:VECTOR_2
		do
			e:=2.7182818284590452353602874713527
			RESULT:= 20*e^(-((labyrinth_pos_x - x)^2/200 + (labyrinth_pos_y-y)^2/200))
		end
end
