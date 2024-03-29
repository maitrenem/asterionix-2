package com.asterionix.controllers.response;


import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import com.asterionix.controllers.util.Agent;
import com.asterionix.controllers.util.Queue;
import com.asterionix.controllers.util.Queues;
import com.asterionix.dao.AgentEntity;
import com.asterionix.dao.AgentRepository;
import com.asterionix.dao.PhoneEntity;
import com.asterionix.dao.PhoneRepository;
import com.asterionix.dao.QueueloginEntity;
import com.asterionix.dao.QueueloginRepository;



public class QueueShowAction extends AbstractAction implements Action {

	private AgentRepository agentRepository;
	private PhoneRepository phoneRepository;
	private QueueloginRepository queueloginRepository;
		
	public QueueShowAction(Socket socket, AgentRepository agentRepository, PhoneRepository phoneRepository, QueueloginRepository queueloginRepository) {

		super(socket);
		
		this.agentRepository = agentRepository;
		this.phoneRepository = phoneRepository;
		this.queueloginRepository = queueloginRepository;
		action = this;
	}

	public AsteriskResponse buildActionResponse(){
		
		QueueShowResponse response = new QueueShowResponse(this);
		
		boolean flag = false;
		
		Queues queues = new Queues();
		
		List<Agent> agents = null;
		
		Queue queue = null;
		for(int k=0 ; k < buffer.size(); k++){

			String lines =  buffer.get(k);
			 	
			 if (lines.contains("holdtime") && lines.contains("talktime")){
			 		
			 		 String arr = lines.split(" ")[0];
				 
			 		 queue = new Queue(arr);
			 		
			 		 agents = new ArrayList<Agent>();
			 		
			 	 }
				 if (lines.contains("Members:")){
					 flag = true;
					 continue;
				 }
				
				 if (flag && !lines.contains("No Callers") ){
					 if (!lines.contains("Callers:")){
						String[] arr =  lines.split("\\s+");
						String num = arr[1];
						String inuse = null;
						if (lines.contains("In use")){
							inuse = "AST_DEVICE_INUSE";
						}
						if (lines.contains("Not in use")){
							inuse = "AST_DEVICE_NOT_INUSE";
						}
						if (lines.contains("Ringing")){
							inuse = "AST_DEVICE_RINGING";
						}
						String name = "";
						try{
						
							DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:01");
							Calendar cal = Calendar.getInstance();
							String d = dateFormat.format(cal.getTime());
							PhoneEntity phoneEntity = phoneRepository.findByPnumber(num);
							//List<QueueloginEntity> queueloginEntity = queueloginRepository.findByPidOrderById(phoneEntity.getId());
							List<QueueloginEntity> queueloginEntity = queueloginRepository.findByPidAndActiontimeGreaterThanOrderById(phoneEntity.getId(),d);
							int aid = queueloginEntity.get(queueloginEntity.size()-1).getAid();
						
							AgentEntity a = agentRepository.findById(aid);
							name = a.getName();
							
							//name="agent1";
								
							 }catch(Exception e){
								 name = "not found";
						}
						Agent agent = new Agent(num, name , inuse);
						agents.add(agent);
						queue.setAgent(agents);
					 }
				 }
				 if (lines.contains("No Callers") || lines.contains("Callers:")){
					 
					 queues.addQueue(queue);
					 
					 flag = false;
					 
					 continue;
				 }
		}

		response.setQueues(queues);
		
		return response;
		
	}
	@Override
	public String getCommand() {
		
		 return "Action: COMMAND\r\ncommand: queue show\r\n\r\n";
	}
	public void fillResponseBuffer(String line, BufferedReader reader) {
		
		buffer.clear();
		
		try {
			buffer.add(line);
		
			String s;
			while(!(s = reader.readLine()).contains("--END COMMAND--")){
				
				buffer.add(s);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
	}

}
