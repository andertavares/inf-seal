package sealbot;

public class Car
{
	private int sector;
	private double distance;
	private double lastDistance;
	private double velocity; // [meters per tick]
	private double direction;
	private boolean unseen;
	private int unseenCounter = 0;

	public Car(int s, double d)
	{
		sector = s;
		distance = d;
		velocity = 0;
		direction = 0;
		unseen = false;
		unseenCounter = 0;
	}

	public void setUnseen(boolean u)
	{
		if(unseen && u) {
			unseenCounter++;
			return;
		}
		unseen = u;
		if(unseen)
			unseenCounter = 1;
		else
			unseenCounter = 0;
	}

	private double expectedSector()
	{
		double phi = Math.PI - direction + getAngle();
		double angle = getAngle() + Math.asin(Math.sin(phi) * velocity 
				/ expectedDistance());
		return (angle / Math.PI * 18) - 18;
	}
	
	private double expectedDistance()
	{
		double phi = Math.PI + getAngle() - direction;
		double loc = lawOfCosines(distance, velocity, phi);
		if(loc==0)
			loc =.1;
		return loc;
	}
	
	public double distanceToExpetation(Car c)
	{
		if (velocity == 0)
		{
			double alpha = (c.getSector()-sector) * 10 * Math.PI/180;
			return lawOfCosines(distance, c.getDistance(), alpha);
		}
		else
		{
			double alpha = ((c.getSector()-expectedSector()) * 10) * Math.PI / 180;
			return lawOfCosines(expectedDistance(), c.getDistance(), alpha);
		}
	}
	
	public void update(Car c)
	{
		double alpha = (c.getSector()-sector) * 10 * Math.PI/180;
		velocity = lawOfCosines(distance, c.getDistance(), alpha);
		if(velocity==0)
			velocity = 0.1;
		
		double phi = Math.asin(Math.sin(alpha) * c.getDistance() / velocity);
		direction = Math.PI + getAngle() - phi;
		
		sector = c.getSector();
		lastDistance = distance;
		distance = c.getDistance();
		
		unseen = false;
		unseenCounter = 0;
	}
	
	private double lawOfCosines(double a, double b, double gamma)
	{
		return Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2) - 2*a*b*Math.cos(gamma));
	}
	
	public int getSector()
	{
		return sector;
	}
	
	public double getAngle()
	{
		return (sector*10 - 175) * Math.PI/180;
	}

	public double getDistance()
	{
		return distance;
	}
	
	public boolean isVisible()
	{
		return (!unseen);
	}
	
	public int getUnseenCounter() {
		return unseenCounter;
	}
	
	public double getVelocity()
	{
		return velocity;
	}
	
	public double getDirection()
	{
		return direction;
	}
	
	public double getRelativeVelocity()
	{
		return distance - lastDistance;
	}
	
	public int getLeftCorrespondingTrackSensor()
	{
		if (sector < 9 || sector > 26)
			return -1;
		return sector - 9;
	}
	
	public int getRightCorrespondingTrackSensor()
	{
		if (sector < 9 || sector > 26)
			return -1;
		return sector - 8;
	}
}