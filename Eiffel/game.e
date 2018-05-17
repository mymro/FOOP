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
		end

	draw_display(pixmap: separate EV_PIXMAP_ADVANCED)
		local
			r: REAL_32
			g: REAL_32
			b: REAL_32
		do
			r:= ((rand.item\\10+1)/10).truncated_to_real
			rand.forth
			g:= ((rand.item\\10+1)/10).truncated_to_real
			rand.forth
			b:= ((rand.item\\10+1)/10).truncated_to_real
			rand.forth
			pixmap.set_foreground_color_rgb(r,g,b)
			pixmap.fill_rectangle (0, 0, pixmap.width, pixmap.height)
			pixmap.set_foreground_color_rgb(1,0,0)
			pixmap.set_line_width (10)
			pixmap.draw_line(0,0,200, 200,false)
			pixmap.set_foreground_color_rgb(0,0,1)
			pixmap.draw_triangle (200, 200, 30)
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
					sleep(1000000000)
					create time.make_now
					--print(time.fine_seconds)
					--print("%N")
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
