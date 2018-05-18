note
	description: "Summary description for {GAME_OBJECT}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	GAME_OBJECT

create
	make

feature {ANY}
	pos_x: INTEGER
	pos_y: INTEGER
	layer: INTEGER

feature {NONE}
	make(x,y,a_layer:INTEGER)
		do
			initialize(x,y,a_layer)
		end

	initialize(x,y,a_layer:INTEGER)
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
			print("test%N")
		end
end
