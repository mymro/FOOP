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
	game_root: MAIN_LABYRINTH -- change to GAME_OBJECT once available

feature{ANY}
	display: detachable separate EV_PIXMAP_ADVANCED
	main_window: detachable separate MAIN_WINDOW
	game_state: INTEGER
		--0 game shut down
		--1 checking everything is fine for game start
		--2 game running

feature {NONE}

	make
		local
			time:TIME
		do
			create time.make_now
			create rand.set_seed (time.seconds)
			game_state := 0
			create game_root.make_with_dimension(create{DIMENSION}.make_with_dimensions (10, 10))
		end

	draw_display(pixmap: separate EV_PIXMAP_ADVANCED)
		do
			pixmap.set_foreground_color_rgb (0,0,0)
			pixmap.fill_rectangle (0, 0, pixmap.width, pixmap.height)
			game_root.draw (pixmap)
		end

feature {ANY}

	shut_down
	-- ends the game
		do
			game_state := 0
		end

	attach_needed_objects(pixmap: separate EV_PIXMAP_ADVANCED; window: separate MAIN_WINDOW)
	--attaches nedded objects for game
	require
		pixmap /= void
		window /= void
	do
		display := pixmap
		main_window := window
	end

	launch
	--launches the game
		require
			game_state = 0
			attached display
			attached main_window
			attached game_state
		local
			time: TIME
			last_time: REAL_64
			current_time: REAL_64
		do
			if attached display as pixmap and
				attached main_window as window then

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
					draw_display(pixmap)
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
