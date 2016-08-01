package de.fhaachen.mazenet.server.userInterface.mazeFX.util;

import de.fhaachen.mazenet.generated.PositionType;
import de.fhaachen.mazenet.server.Board;
import de.fhaachen.mazenet.server.Position;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Richard Zameitat on 01.08.2016.
 */
public class Algorithmics {
	private Algorithmics(){}

	public static List<Position> findPath(Board board, Position from, Position to){
		if(from.equals(to)){
			return Arrays.asList();
		}if(isDirectConnectionAvailable(from,to,board))
			return Arrays.asList(to);

		// Breadth-first search
		HashSet<Position> visited = new HashSet<>();
		ArrayList<LinkedHashSet<Position>> positionsByDistance = new ArrayList<>();
		int dist = 0;
		boolean found = false;
		LinkedHashSet<Position> toVisit = new LinkedHashSet<>();
		toVisit.add(from);
		while(!toVisit.isEmpty()){
			LinkedHashSet<Position> goTo = (LinkedHashSet<Position>) toVisit.clone();
			positionsByDistance.add(dist,goTo);

			// check if target was found
			if(goTo.contains(to)) {
				found = true;
				break;
			}

			// next step
			dist++;
			toVisit.clear();
			visited.addAll(goTo);
			goTo.forEach(p->{
				toVisit.addAll(getDirectReachablePositions(p,board)
						.parallelStream()
						.filter(n->!visited.contains(n)&&!toVisit.contains(n))
						.collect(Collectors.toList()));
			});
		}
		if(!found){
			return Arrays.asList(to);
		}

		// "collect" path
		LinkedList<Position> path = new LinkedList<>();
		path.addFirst(to);
		Position current = to;
		for(int d = dist - 1; d > 0; d--){
			List<Position> connected = getDirectReachablePositions(current,board);
			path.addFirst(current = positionsByDistance.get(d)
					.stream().parallel()
					.filter(connected::contains)
					.findFirst().get());
		}
		return path;
	}

	public static String pathToString(List<? extends PositionType> path){
		return path.stream().sequential()
				.map(pos->String.format("[%d,%d]",pos.getCol(),pos.getRow()))
				.collect(Collectors.joining(" -> "));
	}

	public static boolean isDirectConnectionAvailable(Position a, Position b, Board board){
		/*int rowDelta = b.getRow()-a.getRow();
		int colDelta = b.getCol()-a.getCol();

		CardType aCard = board.getCard(a.getRow(), a.getCol());
		CardType.Openings aOpen = aCard.getOpenings();
		CardType bCard = board.getCard(b.getRow(), b.getCol());
		CardType.Openings bOpen = bCard.getOpenings();

		if(rowDelta==1&&colDelta==0){
			return aOpen.isBottom() && bOpen.isTop();
		}else if(rowDelta==-1&&colDelta==0){
			return aOpen.isTop() && bOpen.isBottom();
		}else if(rowDelta==0&&colDelta==1){
			return aOpen.isRight() && bOpen.isLeft();
		}else if(rowDelta==0&&colDelta==-1){
			return aOpen.isLeft() && bOpen.isRight();
		}
		return false;/**/
		return getDirectReachablePositions(a,board).contains(b);
	}

	private static List<Position> getDirectReachablePositions(PositionType position, Board board){
		// TODO: sorry for this, but I didn't want to change your API ...
		try {
			Method m = Board.class.getDeclaredMethod("getDirectReachablePositions",PositionType.class);
			m.setAccessible(true);
			return ((List<PositionType>) m.invoke(board,position))
					.stream()
					.sequential()
					.map(p->(Position)(p instanceof Position ? p : new Position(p)))
					.collect(Collectors.toList());
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
			return new LinkedList<>();
		}
	}
}
