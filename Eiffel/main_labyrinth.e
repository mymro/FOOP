note
	description: "Root object for the game. Conatins the labyrinth"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"
--missing a lot of features from the java version
class
	MAIN_LABYRINTH
inherit
	GAME_OBJECT
		rename
			make as make_game_object
		redefine
			draw,
			update
		end
	LABYRINTH
		rename
			make as make_labyrinth,
			dimension as labyrinth_dimension,
			get_dimension_x as get_labyrinth_dim_x,
			get_dimension_y as get_labyrinth_dim_y
		redefine
			create_new_labyrinth
		end

create
	make

feature {ANY}
	-- distance between two nodes x axis in pixel
	step_width: INTEGER
	-- distance between two nodes y axis in pixel
	step_height: INTEGER


feature {NONE}

	make(a_game: GAME; a_labyrinth_dimension, a_pos, a_dimension: VECTOR_2)
		require
			a_labyrinth_dimension.x >=1
			a_labyrinth_dimension.y >=1
			a_dimension.x > 0
			a_dimension.y > 0
		do
			make_game_object(a_game, a_pos, a_dimension, create{VECTOR_2}.make_with_xy (0, 0), 0, 1)
			step_width:= 0
			step_height:= 0
			make_labyrinth (a_labyrinth_dimension)
			create_labyrinth

			reset_buffer
		end

	fill_buffer_labyrinth(buffer: separate EV_PIXMAP_ADVANCED)
	--draws labyrinth to a buffer
		require
			(buffer.height = dimension.y-margin.y*2)
			(buffer.width = dimension.x-margin.x*2)
			(buffer.width/labyrinth_dimension.x).truncated_to_integer > 2
			(buffer.height/labyrinth_dimension.y).truncated_to_integer > 2
		local
			step_width_half: INTEGER_32
			step_height_half: INTEGER_32
			x_finish: INTEGER_32
			y_finish: INTEGER_32
			current_node: LABYRINTH_NODE
			center_x: INTEGER_32
			center_y: INTEGER_32
		do
			step_width := (buffer.width/labyrinth_dimension.x).truncated_to_integer
			step_width_half := (step_width/2).truncated_to_integer
			step_height := (buffer.height/labyrinth_dimension.y).truncated_to_integer
			step_height_half := (step_height/2).truncated_to_integer
			x_finish := 0
			y_finish:= 0
			if step_width < step_height then
				buffer.set_line_width (step_width-2)
			else
				buffer.set_line_width (step_height-2)
			end
			buffer.set_foreground_color_rgb (0,0,0)
			buffer.fill_rectangle (0, 0, buffer.width, buffer.height)
			buffer.set_foreground_color_rgb (1, 1, 1)

			across
				1 |..| labyrinth_dimension.x as i
			loop
				across
					 1 |..| labyrinth_dimension.y as j
				loop
					current_node := labyrinth[i.item,j.item]
					center_x := (current_node.x - 1) * step_width + step_width_half
					center_y := (current_node.y - 1) * step_height + step_height_half

					if current_node.neighbours.at ("up") /= void then
						buffer.draw_segment (center_x, center_y, center_x, center_y - step_height_half)
					end
					if current_node.neighbours.at ("down") /= void then
						buffer.draw_segment (center_x, center_y, center_x, center_y + step_height_half)
					end
					if current_node.neighbours.at ("left") /= void then
						buffer.draw_segment (center_x, center_y, center_x - step_width_half, center_y)
					end
					if current_node.neighbours.at ("right") /= void then
						buffer.draw_segment (center_x, center_y, center_x + step_width_half, center_y)
					end

					if current_node.is_of_type (node_type_helper.type_finish)  then
						x_finish := (current_node.x-1) * step_width
						y_finish := (current_node.y-1) * step_height
						if step_width < step_height then
							y_finish := y_finish + step_height_half - step_width_half
						elseif step_width > step_height then
							x_finish := x_finish + step_width_half - step_height_half
						end
					end
				end
			end

			buffer.set_foreground_color_rgb (1, 0, 0)
			if step_width < step_height then
				buffer.fill_ellipse (x_finish, y_finish, step_width, step_width)
			else
				buffer.fill_ellipse (x_finish, y_finish, step_height, step_height)
			end
		end
feature{ANY}
	create_new_labyrinth
		do
			PRECURSOR{LABYRINTH}
			reset_buffer
		end

feature {ANY}

	reset_buffer
	-- redraws the buffer
		do
			if attached game.get_buffer (buffer_indices[1]) as buffer_pixmap then
				fill_buffer_labyrinth(buffer_pixmap)
			else
				print("buffer not attached in main labyrinth")
			end
		end

	draw
	-- draw to display
		do
			draw_buffer_at_index(1)
			PRECURSOR{GAME_OBJECT}
		end

	update
		do
			PRECURSOR{GAME_OBJECT}
		end
end
