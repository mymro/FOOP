note
	description: "A flag that repels"
	author: "Constantin Budin"
	date: "22.05.2018"
	revision: "1.0"

class
	FLAG_DONT_COME_NEAR

inherit
	FLAG

create
	make_flag

feature {NONE}
	make_flag(a_game: GAME; a_pos_in_labyrinth: VECTOR_2; a_labyrinth:MAIN_LABYRINTH)
		do
			make(a_game, a_pos_in_labyrinth, create{EV_COLOR}.make_with_rgb (1, 0, 0), a_labyrinth)
		end

feature {ANY}

	get_modifier_at(x,y:INTEGER):REAL_64
		local
			e:REAL_64
		do
			e:=2.7182818284590452353602874713527
			RESULT:= 20*e^(-((pos_in_labyrinth.x- x)^2/200 + (pos_in_labyrinth.y-y)^2/200))
		end
end
