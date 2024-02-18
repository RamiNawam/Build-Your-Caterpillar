package assignment2;

import java.util.Iterator;
import java.util.Random;
import java.awt.Color;

import assignment2.food.*;


public class Caterpillar extends MySinglyLinkedList<Segment> {

	public EvolutionStage stage;
	public MyStack<Position> positionsPreviouslyOccupied;
	public int goal;
	public int turnsNeededToDigest;

	public static Random randNumGenerator = new Random(1);

	// Creates a Caterpillar with one Segment.  It is up to students to decide how to implement this. 
	public Caterpillar(Position p, Color c, int goal) {
		this.addFirst(new Segment(p, c));
		this.stage = EvolutionStage.FEEDING_STAGE;  // assuming FEEDING_STAGE is one of the stages
		this.positionsPreviouslyOccupied = new MyStack<>();
		this.goal = goal;
		this.turnsNeededToDigest = 0;
	}

	public EvolutionStage getEvolutionStage() {
		return this.stage;
	}

	public Position getHeadPosition() {
		return ((Segment)this.head.element).getPosition() ;
	}

	// returns the color of the segment in position p. Returns null if such segment does not exist
	public Color getSegmentColor(Position p) {
		for (Segment segment : this) {
			if (segment.getPosition().equals(p)) {
				return segment.getColor();
			}
		}
		return null;  // if no segment is found at the position
	}



	// shift all segments to the previous position while maintaining the old color
	public void move(Position p) {
		// Check if the new position is adjacent to the head's position
		Position headPosition = getHeadPosition();
		if (!isAdjacent(headPosition, p)) {
			throw new IllegalArgumentException("New position is out of reach.");
		}

		// Check for collision with the body
		if (containsPosition(p)) {
			// Set caterpillar to ENTANGLED stage and exit
			this.stage = EvolutionStage.ENTANGLED;
			return;
		}

		// No collision, move the caterpillar
		// Add the current head position to the stack
		positionsPreviouslyOccupied.push(headPosition);

		// Shift all segments forward
		Position previousPosition = p;
		MySinglyLinkedList<Segment>.SNode current = head;
		while (current != null) {
			Position temp = current.element.getPosition();
			current.element.setPosition(previousPosition);
			previousPosition = temp;
			current = current.next;
		}

		// Handle special conditions like digesting cake
		if (turnsNeededToDigest > 0) {
			// Add a new segment with a random color at the tail
			Color newColor = GameColors.SEGMENT_COLORS[randNumGenerator.nextInt(GameColors.SEGMENT_COLORS.length)];
			this.addLast(new Segment(positionsPreviouslyOccupied.isEmpty() ? null : positionsPreviouslyOccupied.pop(), newColor));
			turnsNeededToDigest--;

			// Check if the caterpillar reached its goal
			if (this.length >= goal) {
				this.stage = EvolutionStage.BUTTERFLY;
			}
		} else if (this.stage == EvolutionStage.GROWING_STAGE && turnsNeededToDigest == 0) {
			// Resume FEEDING_STAGE if digesting is completed
			this.stage = EvolutionStage.FEEDING_STAGE;
		}
	}

	private boolean isAdjacent(Position pos1, Position pos2) {
		return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY()) == 1;
	}



	private boolean containsPosition(Position p) {
		for (Segment segment : this) {
			if (segment.getPosition().equals(p)) {
				return true;
			}
		}
		return false;
	}
	
	// a segment of the fruit's color is added at the end
	public void eat(Fruit f) {
		Position newPosition = positionsPreviouslyOccupied.isEmpty() ? null : positionsPreviouslyOccupied.pop();
		this.addLast(new Segment(newPosition, f.getColor()));
	}

 
	// the caterpillar moves one step backwards because of sourness
	public void eat(Pickle p) {
		if (positionsPreviouslyOccupied.isEmpty()) return;

		Position newHeadPosition = positionsPreviouslyOccupied.pop();
		SNode current = head;

		while (current.next != null) {
			current.element.setPosition(current.next.element.getPosition());
			current = current.next;
		}

		head.element.setPosition(newHeadPosition);
	}

	// all the caterpillar's colors are shuffled around
	public void eat(Lollipop lolly) {
		Color[] colors = new Color[this.length];
		int i = 0;
		for (Segment segment : this) {
			colors[i++] = segment.getColor();
		}

		// Fisher-Yates shuffle
		for (i = colors.length - 1; i > 0; i--) {
			int j = randNumGenerator.nextInt(i + 1);
			Color temp = colors[i];
			colors[i] = colors[j];
			colors[j] = temp;
		}

		i = 0;
		for (Segment segment : this) {
			segment.setColor(colors[i++]);
		}
	}

	// brain freeze!!
	// It reverses and its (new) head turns blue
	public void eat(IceCream gelato) {
		this.reverse();
		if (head != null) {
			head.element.setColor(Color.BLUE); // Assuming Color.BLUE is a valid color
		}
		 // Assuming Color.BLUE is a valid color
		positionsPreviouslyOccupied.clear();
	}

	public void reverse() {
		if (head == null || head.next == null) {
			return; // No need to reverse if the list is empty or has only one element
		}

		SNode prev = null;
		SNode current = head;
		SNode next = null;

		while (current != null) {
			next = current.next; // Store next node
			current.next = prev; // Reverse the current node's pointer
			prev = current; // Move pointers one position ahead
			current = next;
		}

		head = prev; // Reset the head of the list to the new front element
	}
 
	// the caterpillar embodies a slide of Swiss cheese loosing half of its segments. 
	public void eat(SwissCheese cheese) {
		SNode current = head;
		while (current != null && current.next != null) {
			positionsPreviouslyOccupied.push(current.next.element.getPosition());
			current.next = current.next.next;
			current = current.next;
		}
	}

	// A big growing stage begins
	public void eat(Cake cake) {
		// Assumption: Energy provided by the cake is 1 for each segment to be grown
		int energy = 1;

		while (energy > 0 && !positionsPreviouslyOccupied.isEmpty()) {
			Position newPosition = positionsPreviouslyOccupied.pop();
			Color randomColor = GameColors.SEGMENT_COLORS[randNumGenerator.nextInt(GameColors.SEGMENT_COLORS.length)];
			this.addLast(new Segment(newPosition, randomColor));
			energy--;

			// Check if the caterpillar reached its goal and update stage if necessary
			if (this.length >= goal) {
				this.stage = EvolutionStage.BUTTERFLY;
				return;
			}
		}

		// Update turnsNeededToDigest with remaining energy
		turnsNeededToDigest = energy;

		// Check if the caterpillar has reached the butterfly stage
		if (this.length >= goal) {
			this.stage = EvolutionStage.BUTTERFLY;
		} else {
			// Resume feeding stage
			this.stage = EvolutionStage.FEEDING_STAGE;
		}
	}

 	public String toString() {

 		String gus = "Gus: " ;
 		Iterator i = this.iterator() ;

 		while ( i.hasNext() ) {
			Segment s = (Segment) i.next() ;
			gus = s.toString() + gus ;
 		}
		return gus;
 	}
}
