note
	description: "Main game Loop and communication with GUI"
	author: "Constantin Budin"
	date: "17.05.2018"
	revision: "0.1"

class
	GAME
	inherit
    	EXECUTION_ENVIRONMENT
    	rename
    		launch as ex_launch
    	end
create
	make

feature{NONE}
	rand: RANDOM
	game_root: detachable MAIN_LABYRINTH

feature{ANY}
	-- the main window
	labyrinth_nodes_x: INTEGER = 10
	labyrinth_nodes_y: INTEGER = 10
	fps_limit: INTEGER = 60
	main_window: detachable separate MAIN_WINDOW
	draw_area_height: INTEGER
	draw_area_width: INTEGER
	game_state: INTEGER
		--0 game shut down
		--1 checking everything is fine for game start
		--2 game running

feature {NONE}

	make
	-- eeding random number generator
	-- and resetting state
		local
			time:TIME
		do
			create time.make_now
			create rand.set_seed (time.seconds)
			game_state := 0
			draw_area_height:= 0
			draw_area_width:=0
		end

	draw_display
	-- the main draw funcrion
	-- calls draw on root object
		do
			if attached game_root as root then
				root.draw
			else
				print("root object not attached in game in draw_display%N")
			end
		end

	get_buffer_i(i:INTEGER; window: separate MAIN_WINDOW): detachable separate EV_PIXMAP_ADVANCED
	--implementation of get_buffer
		do
			RESULT:=window.get_pixmap_buffer(i)
		end

	draw_buffer_to_display_i(i: INTEGER; pos_x, pos_y:INTEGER; window: separate MAIN_WINDOW)
	--implementation of draw_buffer_to_display
		do
			window.draw_buffer_to_display (i, pos_x, pos_y)
		end

feature {ANY}

	get_buffer(i:INTEGER): detachable separate EV_PIXMAP_ADVANCED
	-- returns the requested buffer. If it doesnt exist void
		do
			if attached main_window as window then
				RESULT:= get_buffer_i(i, window)
			else
				print("main_window not attached in game")
				RESULT:= void
			end
		end

	draw_buffer_to_display(i: INTEGER; pos:VECTOR_2)
	-- draws a buffer to display with top left corner position of pos
		do
			if attached main_window as window then
				draw_buffer_to_display_i(i, pos.x, pos.y, window)
			else
				print("main_window not attached in game draw_bufer_to_display")
			end
		end

	shut_down
	-- ends the game
		do
			game_state := 0
		end

	set_up(window: separate MAIN_WINDOW; drawing_area_height, drawing_area_width:INTEGER)
	--attaches nedded objects for game
	--creates all game objects
	require
		window /= void
	local
		buffer_indices: ARRAY[INTEGER]
		flag: FLAG
	do
		main_window:=window
		draw_area_height:= drawing_area_height
		draw_area_width:= drawing_area_width
		-- create buffers
		create buffer_indices.make_filled (0, 1, 1)
		buffer_indices[1]:= window.create_pixmap_buffer(drawing_area_height, drawing_area_width)
		--buffer_index:= window.create_pixmap_buffer_from_image ("images\missing_image.png")
		create game_root.create_new_labyrinth(Current, create{VECTOR_2}.make_with_xy (labyrinth_nodes_x, labyrinth_nodes_y), create{VECTOR_2}.make_with_xy (0, 0), 0, buffer_indices)
		create buffer_indices.make_filled (0, 1, 1)
		buffer_indices[1]:= window.create_pixmap_buffer_from_image ("images\missing_image.png")
		create flag.make (current, create{VECTOR_2}.make_with_xy (0, 0), 0, buffer_indices)
		if attached game_root as root then
			root.add_child (flag)
		end

	end

	launch-- TODO needs frame limiter
	--launches the game
	-- the main game loop
		require
			game_state = 0
			attached main_window
		local
			time: TIME
			last_time: REAL_64
			current_time: REAL_64
			time_diff: REAL_64
			frame_time: REAL_64
		do
			frame_time:= 1/fps_limit
			if	attached main_window as window then

				game_state := 1

				from
				until
					is_running(window)
				loop

				end

				game_state := 2

				from
				until
				 	not is_running(window)
				loop
					create time.make_now
					current_time:= time.fine_seconds
					time_diff:= current_time-last_time
					-- frame limiter
					if time_diff < frame_time then
						sleep(((frame_time-time_diff)*1000000000).truncated_to_integer_64)
						create time.make_now
						current_time:= time.fine_seconds
					end

					last_time:=current_time
					draw_display
				end
				game_state := 0
			end
		end

feature {NONE}

	is_running(window: separate MAIN_WINDOW):BOOLEAN
		do
			Result:= (game_state /= 0) and (not window.is_destroyed)
		end
end
