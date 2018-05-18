note
	description: "basic game_object functionality"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"

class
	GAME_OBJECT-- has to be redone completely
	--not yet implemented

create
	make

feature {ANY}
	pos_x: REAL_64
	pos_y: REAL_64
	layer: INTEGER

feature {NONE}
	make(x,y:REAL_64; a_layer:INTEGER)
		do
			initialize(x,y,a_layer)
		end

	initialize(x,y:REAL_64; a_layer:INTEGER)
	require
		x>=1
		y>=1
	do
		pos_x:= x
		pos_y:= y
		layer:= a_layer
	end
feature {ANY}
	draw(display: separate EV_PIXMAP_ADVANCED)
		do
			--print("test%N")
		end
end
