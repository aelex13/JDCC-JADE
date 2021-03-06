package auctioneerBehaviours;

import jade.core.Timer;
import jade.core.TimerListener;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import main.Auctioneer;

public class AuctioneerDutchReceiveBids extends Behaviour 
{
	private Auctioneer parent;
	private Timer timer;
	
	int replies = 0;
	
	
	
	public AuctioneerDutchReceiveBids(Auctioneer agent) 
	{
        super(agent);
        parent = agent;
        this.timer = new Timer(System.currentTimeMillis() + 1000, new TimerListener() {
			
			@Override
			public void doTimeOut(Timer arg0) {
				parent.bidTimerRanOut = true;
			}
		});
    }
	
	public void action()
	{
		// check if the bid proposals were already sent to the bidders
		if(parent.bidProposalSent)
		{
			// receive replies
			ACLMessage msg = myAgent.receive(parent.mt);
			//System.out.println("AuctioneerDutchReceiveBids mt: " + parent.mt);
			
			// check if message is empty
			if (msg != null) 
			{
			//System.out.println("AuctioneerReceiveBids: " + msg.toString());
                // check if this reply was a new bid
                if (msg.getPerformative() == ACLMessage.PROPOSE) 
                {
                    int bid = Integer.parseInt(msg.getContent());
                    if (parent.maxBidAgent == null || bid >= parent.maxBid) 
                    {                    	
                        // store as a new max bid
                        parent.maxBid = bid;
                        parent.maxBidAgent = msg.getSender();
                    }

                    // Let the agent know that the bid was received with an inform reply
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("bid received");
                    parent.send(reply);
                }
                else if (msg.getPerformative() == ACLMessage.REFUSE)
                {
                    // only for english and dutch
                	ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("refusal received");
                    parent.send(reply);
                }

                replies++;
            }
            else 
            {
            	//System.out.println("AuctioneerDutchReceiveBids: Blocking!");
                block();
            }
			
			if (replies >= parent.bidders.length) 
			{
                // We have received all bids
                parent.bidProposalsRepliesReceived = true;
            }
		}
	}
	
	public boolean done() 
	{
        return parent.bidProposalsRepliesReceived || parent.bidTimerRanOut;
    }
}
