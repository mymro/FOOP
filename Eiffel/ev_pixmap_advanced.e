note
	description: "Summary description for {EV_PIXMAP_ADVANCED}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	EV_PIXMAP_ADVANCED

inherit
	EV_PIXMAP

create
	default_create,
	make_with_size,
	make_with_pointer_style,
	make_with_pixel_buffer

feature {ANY}
	set_foreground_color_rgb (r,g,b:REAL_32)--add require
		do
			current.set_foreground_color (create{EV_COLOR}.make_with_rgb (r, g, b))
		end

	draw_line(x_from, y_from, x_to, y_to:INTEGER_32; is_closed:BOOLEAN)
		local
			arr: ARRAY[EV_COORDINATE]
		do
			create arr.make_empty
			arr.force (create {EV_COORDINATE}.make (x_from, y_from), 1)
			arr.force (create {EV_COORDINATE}.make (x_to, y_to), 2)
			current.draw_polyline (arr, is_closed)
		end
end
