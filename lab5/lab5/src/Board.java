import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class Board extends JComponent implements MouseInputListener, ComponentListener {
	private static final long serialVersionUID = 1L;
	private Point[][] points;
	private int size = 10;
	public int editType=0;
	public final static Color FAR = new Color(129, 0, 0);
	public final static Color CLOSE = new Color(255, 255, 0);
	public Board(int length, int height) {
		addMouseListener(this);
		addComponentListener(this);
		addMouseMotionListener(this);
		setBackground(Color.WHITE);
		setOpaque(true);
	}

	public void iteration() {
		unblockAll();
		for (int x = 1; x < points.length - 1; ++x)
			for (int y = 1; y < points[x].length - 1; ++y){
				this.points[x][y].move(); // 0 nothing  1 moved 2 reached exit
				if(this.points[x][y].smoked){
					if(this.points[x][y].smokedCounter >= 5){
						this.points[x][y].smoked = false;
						this.points[x][y].smokedCounter = 0;
					}else{
						this.points[x][y].smokedCounter++;
					}
				}
				if(this.points[x][y].type == 4){
					generateSmoke(x,y);
				}
			}
		this.repaint();
	}

	public void clear() {
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y) {
				points[x][y].clear();
			}
		calculateField();
		this.repaint();
	}

	private void generateSmoke(int idx, int idy){
		int max = 5;
		int min = -4;
		Random random = new Random();
					int x = random.nextInt(max - min) + min;
					int y = random.nextInt(max - min) + min;
					if(valid(x + idx,y + idy)){
						points[x + idx][y + idy].smoked = true;
					}


	}


	private void initialize(int length, int height) {
		points = new Point[length][height];

		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y] = new Point();

		for (int x = 1; x < points.length-1; ++x) {
			for (int y = 1; y < points[x].length-1; ++y ){

				// MOORE
				points[x][y].addNeighbor(points[x-1][y+1]);
				points[x][y].addNeighbor(points[x+1][y-1]);
				points[x][y].addNeighbor(points[x+1][y]);
				points[x][y].addNeighbor(points[x+1][y+1]);
				points[x][y].addNeighbor(points[x][y+1]);
				points[x][y].addNeighbor(points[x][y-1]);
				points[x][y].addNeighbor(points[x-1][y-1]);
				points[x][y].addNeighbor(points[x-1][y]);



				// von Neuman
				/*
				points[x][y].addNeighbor(points[x][y+1]);
				points[x][y].addNeighbor(points[x][y-1]);
				points[x][y].addNeighbor(points[x+1][y]);
				points[x][y].addNeighbor(points[x-1][y]);
				 */





			}
		}	
	}

	private boolean valid(int x, int y){
		int nx = this.points.length;
		int ny = this.points[0].length;
		return x >= 0 && y >= 0 && x < nx && y < ny;
	}

	private void calculateField(){
		ArrayList<Point> toCheck = new ArrayList<Point>();

		for (int x = 0; x < points.length; ++x) {
			for (int y = 0; y < points[x].length; ++y ){
				if(points[x][y].type == 2) {
					points[x][y].staticField = 0;
					int n = points[x][y].neighbors.size();
					for (int i = 0; i < n; i++) {
						if(points[x][y].neighbors.get(i).type != 1 && points[x][y].neighbors.get(i).type != 4) {
							toCheck.add(points[x][y].neighbors.get(i));
						}
					}
				}

			}
		}
		while (!toCheck.isEmpty()){
			if(toCheck.get(0).calcStaticField()){
				int n = toCheck.get(0).neighbors.size();
				for(int i = 0; i < n; i++){
					if(toCheck.get(0).neighbors.get(i).type != 1 && toCheck.get(0).neighbors.get(i).type != 4){
						toCheck.add(toCheck.get(0).neighbors.get(i));
					}
				}
			}
			toCheck.remove(0);
		}
	}

	private void unblockAll(){
		for (int x = 0; x < points.length; ++x)
			for (int y = 0; y < points[x].length; ++y)
				points[x][y].blocked = false;
	}

	protected void paintComponent(Graphics g) {
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		g.setColor(Color.GRAY);
		drawNetting(g, size);
	}

	private void drawNetting(Graphics g, int gridSpace) {
		Insets insets = getInsets();
		int firstX = insets.left;
		int firstY = insets.top;
		int lastX = this.getWidth() - insets.right;
		int lastY = this.getHeight() - insets.bottom;

		int x = firstX;
		while (x < lastX) {
			g.drawLine(x, firstY, x, lastY);
			x += gridSpace;
		}

		int y = firstY;
		while (y < lastY) {
			g.drawLine(firstX, y, lastX, y);
			y += gridSpace;
		}

		for (x = 1; x < points.length-1; ++x) {
			for (y = 1; y < points[x].length-1; ++y) {

				if(points[x][y].type==0){
					float staticField = points[x][y].staticField;
					float intensity = staticField/100;

					int red = (int)Math.abs((intensity * FAR.getRed()) + ((1 - intensity) * CLOSE.getRed()));
					int green = (int)Math.abs((intensity * FAR.getGreen()) + ((1 - intensity) * CLOSE.getGreen()));
					int blue = (int)Math.abs((intensity * FAR.getBlue()) + ((1 - intensity) * CLOSE.getBlue()));
					if (intensity > 1.0) {
						intensity = 1.0f;
						g.setColor(new Color(intensity, intensity,intensity ));
					}else{
						g.setColor(new Color(red, green,blue ));
					};
				}
				else if (points[x][y].type==1){
					g.setColor(Color.magenta);
				}
				else if (points[x][y].type==2){
					g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.7f));
				}
				if(points[x][y].smoked){
					g.setColor(Color.gray);
				}
				if (points[x][y].isPedestrian){
					g.setColor(new Color(0.0f, 0.0f, 1.0f, 0.7f));
				}
				if(points[x][y].type == 4 ){
					g.setColor(Color.red);
				}
				g.fillRect((x * size) + 1, (y * size) + 1, (size - 1), (size - 1));
			}
		}

	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		System.out.println(points[x][y].staticField + "\n");
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			if(editType==3){
				points[x][y].isPedestrian=true;
			}
			else{
				points[x][y].type= editType;
			}
			this.repaint();
		}
	}

	public void componentResized(ComponentEvent e) {
		int dlugosc = (this.getWidth() / size) + 1;
		int wysokosc = (this.getHeight() / size) + 1;
		initialize(dlugosc, wysokosc);
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX() / size;
		int y = e.getY() / size;
		if ((x < points.length) && (x > 0) && (y < points[x].length) && (y > 0)) {
			if(editType==3){
				points[x][y].isPedestrian=true;
			}
			else{
				points[x][y].type= editType;
			}
			this.repaint();
		}
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

}
