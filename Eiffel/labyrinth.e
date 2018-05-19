note
	description: "The labyrinth for our game"
	author: "Constantin Budin"
	date: "18.05.2018"
	revision: "0.1"

class
	LABYRINTH

create
	make

feature {ANY}
	dimension: VECTOR_2

feature {NONE}
	labyrinth: ARRAY2[LABYRINTH_NODE]

feature {NONE}

	make(a_dimension: VECTOR_2)
		require
			a_dimension.x >=1
			a_dimension.y >=1
		do
			dimension:=a_dimension
			create labyrinth.make_filled (create {LABYRINTH_NODE}.make (1, 1, 2), dimension.x, dimension.y)
			across
				1 |..| dimension.x as  i
			loop
				across
					1 |..| dimension.y as j
				loop
					labyrinth.put (create{LABYRINTH_NODE}.make (i.item, j.item, 2), i.item, j.item)
				end
			end
		end

feature {ANY}

	get_node_at alias "[]" (x,y: INTEGER):LABYRINTH_NODE
		require
			x>=1 and x <= dimension.x
			y>=1 and y <= dimension.y
		do
			RESULT:= labyrinth[x,y]
		end
	create_labyrinth
	-- creates a random labyrinth
		local
			time: TIME
			rand: RANDOM
			i: INTEGER
			j: INTEGER
			frontier: ARRAYED_LIST[LABYRINTH_NODE]
			frontier_matrix: ARRAY2[BOOLEAN]
			current_node: LABYRINTH_NODE
			has_run_once: BOOLEAN
			possible_connections : ARRAYED_LIST[LABYRINTH_NODE]
			connect_node : LABYRINTH_NODE
		do
			create time.make_now
			create rand.set_seed (time.seconds)
			i:= 1
			j:= 1
			--create a random starting position on the outskirt
			inspect
				(rand.item \\ 4)
			when 0 then
				i := 1
				rand.forth
				j := (rand.item\\dimension.y) + 1
			when 1 then
				i := dimension.x
				rand.forth
				j := (rand.item\\dimension.y) + 1
			when 2 then
				rand.forth
				i := (rand.item\\dimension.x) + 1
				j := 1
			when 3 then
				rand.forth
				i := (rand.item\\dimension.x) + 1
				j := dimension.y
			end

			labyrinth[i,j].set_type (0)

			-- create the rest of the labyrinth
			create frontier.make (0)
			create frontier_matrix.make_filled (FALSE, dimension.x, dimension.y)
			current_node:=labyrinth[i,j]
			frontier_matrix[i,j]:= TRUE

			from
				has_run_once:=FALSE
			until
				has_run_once and frontier.count = 0
			loop

				if current_node.y-1 >= 1 and (not frontier_matrix[current_node.x, current_node.y-1]) then
						frontier.extend (labyrinth[current_node.x, current_node.y-1])
						frontier_matrix[current_node.x, current_node.y-1] := true;
					end
				if current_node.y+1 <= dimension.y and (not frontier_matrix[current_node.x, current_node.y+1]) then
						frontier.extend (labyrinth[current_node.x, current_node.y+1])
						frontier_matrix[current_node.x, current_node.y+1] := true;
					end
				if current_node.x-1 >= 1 and (not frontier_matrix[current_node.x-1, current_node.y]) then
						frontier.extend (labyrinth[current_node.x-1, current_node.y])
						frontier_matrix[current_node.x-1, current_node.y] := true;
					end
				if current_node.x+1 <= dimension.x and (not frontier_matrix[current_node.x+1, current_node.y]) then
						frontier.extend (labyrinth[current_node.x+1, current_node.y])
						frontier_matrix[current_node.x+1, current_node.y] := true;
					end

				rand.forth
				current_node := frontier[(rand.item\\frontier.count) + 1]
				current_node.set_type (1)
				frontier.go_i_th ((rand.item\\frontier.count) + 1)
				frontier.remove

				create possible_connections.make (0)

				if current_node.y-1>=1 and (labyrinth[current_node.x, current_node.y-1].type /= 2) then
					possible_connections.extend (labyrinth[current_node.x, current_node.y-1])
				end
				if current_node.y+1 <= dimension.y and (labyrinth[current_node.x, current_node.y+1].type /= 2) then
					possible_connections.extend (labyrinth[current_node.x, current_node.y+1])
				end
				if current_node.x-1>=1 and (labyrinth[current_node.x-1, current_node.y].type /= 2) then
					possible_connections.extend (labyrinth[current_node.x-1, current_node.y])
				end
				if current_node.x+1 <= dimension.x and (labyrinth[current_node.x+1, current_node.y].type /= 2) then
					possible_connections.extend (labyrinth[current_node.x+1, current_node.y])
				end

				rand.forth
				connect_node := possible_connections[(rand.item\\possible_connections.count)+1]

				if connect_node.x = current_node.x then
					if connect_node.y > current_node.y then
						current_node.neighbours.put (connect_node, "down")
						connect_node.neighbours.put (current_node, "up")
					else
						current_node.neighbours.put (connect_node, "up")
						connect_node.neighbours.put (current_node, "down")
					end
				else
					if connect_node.x > current_node.x then
						current_node.neighbours.put (connect_node, "right")
						connect_node.neighbours.put (current_node, "left")
					else
						current_node.neighbours.put (connect_node, "left")
						connect_node.neighbours.put (current_node, "right")
					end
				end

				has_run_once := TRUE
			end
		end
end
