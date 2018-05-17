note
	description: "Summary description for {GAME}."
	author: ""
	date: "$Date$"
	revision: "$Revision$"

class
	GAME
	inherit
    	EXECUTION_ENVIRONMENT
    	rename
    		launch as ex_launch
    	end
create
	make

feature {NONE}
	main_window: separate MAIN_WINDOW
	rand: RANDOM
	game_state : separate GAME_STATE
	display: separate EV_PIXMAP_ADVANCED

	make(window: separate MAIN_WINDOW; pixmap: separate EV_PIXMAP_ADVANCED state: separate GAME_STATE)
		require
			window /= void
			state /= void
		do
			main_window := window
			game_state := state
			display := pixmap
			create rand.make
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
	launch
		local
			time: TIME
		do
			from
			until
				is_running(game_state, main_window)
			loop

			end

			from
			until
			 	not is_running(game_state, main_window)
			loop
				sleep(1000000000)
				create time.make_now
				print(time.fine_seconds)
				print("%N")
				draw_display(display)
			end
		end

feature {NONE}

	is_running(state: separate GAME_STATE; window: separate MAIN_WINDOW):BOOLEAN
		do
			Result:= (state.state /= 0) and (not window.is_destroyed)
		end
end
