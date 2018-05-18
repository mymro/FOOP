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
	game_root: detachable MAIN_LABYRINTH -- change to GAME_OBJECT once available

feature{ANY}
	-- the main window
	main_window: detachable separate MAIN_WINDOW

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
		end

	draw_display
	-- the main draw funcrion
	-- calls draw on root object
		do
			if attached game_root as root then
				root.draw
			else
				print("root object not attached in game")
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
		buffer_index: INTEGER
	do
		main_window:=window
		-- create buffers
		buffer_index:= window.create_pixmap_buffer(drawing_area_height, drawing_area_width)
		--buffer_index:= window.create_pixmap_buffer_from_image ("images\missing_image.png")
		create game_root.create_new_labyrinth(Current, void, create{VECTOR_2}.make_with_pos (100, 100), create{VECTOR_2}.make_with_pos (0, 0), buffer_index)
	end

	launch
	--launches the game
	-- the main game loop
		require
			game_state = 0
			attached main_window
		local
			time: TIME
			last_time: REAL_64
			current_time: REAL_64
		do
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
					--sleep(1000000000)
					create time.make_now
					current_time:= time.fine_seconds
					print((1/(current_time-last_time)).out + " FPS%N")
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
