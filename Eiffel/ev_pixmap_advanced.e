note
	description: "adds a few useful functions for multithreading and the game"
	author: "Constantin Budin"
	date: "17.05.18"
	revision: "0.1"

class
	EV_PIXMAP_ADVANCED

inherit
	EV_PIXMAP

create
	default_create,
	make_with_size,
	make_with_pointer_style,
	make_with_pixel_buffer

feature {ANY} -- useful drawing funcions
	set_foreground_color_rgb (r,g,b:REAL_32)
		-- sets the foreground color
		require
			r >= 0 and r <= 1
			g >= 0 and g<= 1
			b >= 0 and b <= 1
		do
			current.set_foreground_color (create{EV_COLOR}.make_with_rgb (r, g, b))
		end

	set_foreground_color_grey (grey:REAL_32)
		require
			grey>=0 and grey <=1
		do
			current.set_foreground_color (create{EV_COLOR}.make_with_rgb (grey, grey, grey))
		end

	draw_triangle(x_center, y_center, triangle_height:INTEGER_32)
		-- draws a filled triangle with (x_center, y_center - height/2)
		--(x_center-height/2, y_center + height/2) and (x_center+height/2, y_center + height/2)
		require
			triangle_height >0
		local
			arr: ARRAY[EV_COORDINATE]
			h_half : INTEGER_32
		do
			h_half := (triangle_height/2).truncated_to_integer
			create arr.make_filled (create {EV_COORDINATE}.make (0, 0), 1, 3)
			arr.enter (create {EV_COORDINATE}.make (x_center, y_center - h_half), 1)
			arr.enter (create {EV_COORDINATE}.make (x_center-h_half, y_center + h_half), 2)
			arr.enter (create {EV_COORDINATE}.make (x_center+h_half, y_center + h_half), 3)
			current.fill_polygon (arr)
		end

end
