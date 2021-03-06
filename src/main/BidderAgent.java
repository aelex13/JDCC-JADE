package main;
import java.util.Random;

import bidderBehaviours.BidderAgentLoser;
import bidderBehaviours.BidderAgentReceiveBidProposal;
import bidderBehaviours.BidderAgentWinner;
import bidderBehaviours.DutchBidderAgentReceiveBidProposal;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class BidderAgent extends Agent 
{
	public int money;
	public int initMoney;
	public int[] preferences;
	int moneyOffsetPercentage = 10;
	
	public Database.AuctionDatabase db;
	
	protected void setup()
	{
		// Create the items database
		db = new Database.AuctionDatabase();
		
		// Register the agent
		DFAgentDescription df = new DFAgentDescription();
		df.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("auctionBidderAgent");
		sd.setName("auctionBidderAgent");
		df.addServices(sd);
		try 
		{
			DFService.register(this, df);
		}
		catch (FIPAException fe) 
		{
			fe.printStackTrace();
		}
		
		// generate random money
		generateMoney();
		
		//
		generatePreferences();
		
			
			//Add behaviour to receive bid proposal messages
			addBehaviour(new BidderAgentReceiveBidProposal(this));
			
			// Add behaviour to receive the winning message if this agent won the auction
			addBehaviour(new BidderAgentWinner(this));
			
			// Add behaviour to receive the losing message if this agent lost the auction
			addBehaviour(new BidderAgentLoser(this));
	}
	
	private void generateMoney()
	{
		int totalPrice = 0;
		int moneyAverage;
		int moneyMin;
		int moneyMax;
		int moneyOffset;

		// First get the total price of all items
		for(int i=0; i < db.getItems().length; i++)
		{
			totalPrice += db.getItems()[i].getStartingPrice();
		}
		
		// Calculate the min and max of the possible amount of money this agent can have
		moneyAverage = totalPrice/db.getBidderNumber();
		
		// Calculate the offset based on the selected percentage, and calculate min and max
		moneyOffset = moneyAverage*moneyOffsetPercentage/100;
		moneyMin = moneyAverage - moneyOffset;
		moneyMax = moneyAverage + moneyOffset;
		
		// Generate a random based on min and max
		Random rd = new Random();
		money = moneyMin + rd.nextInt(moneyMax-moneyMin);
		initMoney = money;
	}
	
	private void generatePreferences()
	{
		preferences = new int[db.getItems().length];
		Random rd = new Random();
		
		// fill the preferences table
		for(int i=0; i < preferences.length; i++)
		{
			preferences[i] = rd.nextInt(100);
		}
	}
}
