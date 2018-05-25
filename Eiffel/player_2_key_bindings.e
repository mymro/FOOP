note
	description: "Key bindings for player 2"
	author: "Constantin Budin"
	date: "25.05.2018"
	revision: "1.0"

class
	PLAYER_2_KEY_BINDINGS

inherit
	KEY_BINDINGS

feature{ANY}
	up_code: INTEGER = 58 -- up
	down_code: INTEGER = 59 -- down
	left_code: INTEGER = 60 -- left
	right_code: INTEGER = 61 -- right
	place_repell_flag_code: INTEGER = 49 -- .
	place_attract_flag_code: INTEGER = 57 -- -
end
