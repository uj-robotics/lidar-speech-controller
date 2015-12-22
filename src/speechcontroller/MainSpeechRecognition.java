package speechcontroller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;

public class MainSpeechRecognition {
	
	static int left_motor;
	static int right_motor;
	static JSONObject packet = new JSONObject();
	
	//Path to acoustic mode
	private static final String ACOUSTIC_MODEL = 
			"resource:/edu/cmu/sphinx/models/en-us/en-us";
	//Path to dictionary
	private static final String DICTIONARY_PATH = 
			"resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
	//Language model
	private static String LANGUAGE_MODEL = 
			"resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin";
	private static String GRAMMAR_PATH = 
			"grammar/";
	
	//To check if utterance matches orders
	private static boolean utteranceRegex(String direction, String utterance) {
		
		Pattern pattern = Pattern.compile("(move\\s|turn\\s)*+"+direction+"(\\splease)*");
		
		Matcher matcher = pattern.matcher((CharSequence) utterance);
		
		return matcher.matches() ?  true : false;
	}	
	
	private static void connection(int left_motor, int right_motor, JSONObject packet) {
		try {
			packet.put("left_motor", left_motor);
			packet.put("right_motor", right_motor);
			Client client = new Client(packet);
			client.run();
			new Thread(client).start();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void recognizeOrders(LiveSpeechRecognizer recognizer, String utterance) {
		
		//Catch utterance until 'stop' or 'break' is said
		while(true) {
			
			utterance = recognizer.getResult().getHypothesis();
			
			if(utterance.startsWith("stop") || utterance.equals("break")) break;
			
			else if(utteranceRegex("forward", utterance)) {
				
				left_motor = 250;
				right_motor = 250;
				
				connection(left_motor, right_motor, packet);
				
    		} else if(utteranceRegex("backward", utterance)) {
    			left_motor = -250;
    			right_motor = -250;
    			
    			connection(left_motor, right_motor, packet);
    			
    		} else if(utteranceRegex("left", utterance)) {
    		
    			left_motor = -250;
    			right_motor = 250;    
    			
    			connection(left_motor, right_motor, packet);
    			
    		} else if(utteranceRegex("right", utterance)) {
    			
    			left_motor = 250;
    			right_motor = -250;		
    			
    			connection(left_motor, right_motor, packet);
    			
    		} else if(utterance.startsWith("wait") || utterance.equals("please wait")) {
    			left_motor = 0;
    			right_motor = 0;  	
    			
    			connection(left_motor, right_motor, packet);
    		}		
		}
	}
	
    public static void main(String[] args) throws Exception {
	
    	Configuration configuration = new Configuration();

        configuration
                .setAcousticModelPath(ACOUSTIC_MODEL);
        configuration
                .setDictionaryPath(DICTIONARY_PATH);
        configuration
                .setLanguageModelPath(LANGUAGE_MODEL);
        configuration
        		.setGrammarPath(GRAMMAR_PATH);
        
        //To catch words only from grammar file
        configuration.setUseGrammar(true);
        configuration.setGrammarName("grammar");

        LiveSpeechRecognizer recognizer = new LiveSpeechRecognizer(configuration);
        recognizer.startRecognition(true);   
           	
        String utterance = recognizer.getResult().getHypothesis();
      	
        recognizeOrders(recognizer, utterance);    
        
        recognizer.stopRecognition();
    }
 
}


