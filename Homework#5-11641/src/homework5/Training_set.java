package homework5;

/*
 * This class is designed to read training files and store all the data in six two-dimension array lists
 * 
 */
import java.io.*;
import java.util.ArrayList;

public class Training_set {

	private ArrayList<ArrayList<Integer>> Movie_User_list = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> Movie_User_Score = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> MovieID = new ArrayList<Integer>();
	
	private ArrayList<ArrayList<Integer>> User_Movie_list = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<Integer>> User_Movie_Score = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> UserID = new ArrayList<Integer>();
	/*This variable is the average of all ratings */
	private double total_average;
	/*This is the number of total ratings  */
	private double total_num;
	/*Constructor */
	public Training_set()
	{
		this.total_average = 0;
		this.total_num = 0;
	}
	/*This function is responsible for reading files and store them in lists */
	public void Reading_Training_Set()
	{
		String path = new String("Training_Set/");
		int count = 0;
		int row_counter = 0;
		
		while(count <= 17765)
		{
			String tmp_str = Integer.toString(count);
			while(tmp_str.length() < 7)
				tmp_str = "0" + tmp_str;
			tmp_str = path + "mv_" + tmp_str + ".txt";
			File train_file = new File(tmp_str);
			try{
				
				FileInputStream fist = new FileInputStream(train_file);     
			    InputStreamReader isrt = new InputStreamReader(fist, "UTF-8");
			    BufferedReader readert = new BufferedReader(isrt);
			    tmp_str = readert.readLine();
			    int counter = 1;
			    while((tmp_str = readert.readLine()) != null)
			    {
			    	int i = get_substring(tmp_str,',');
			    	int userid =  Integer.valueOf(tmp_str.substring(0, i));
			    	int score = Integer.valueOf(tmp_str.substring(i + 1, i + 2));
			    	if(counter == 1)
			    	{
			    		/*add new numbers */
			    		this.Movie_User_list.add(new ArrayList<Integer>());
			    		this.Movie_User_list.get(row_counter).add(userid);
			    		this.Movie_User_Score.add(new ArrayList<Integer>());
			    		this.Movie_User_Score.get(row_counter).add(score);
			    		this.MovieID.add(count);
			    	
			    	}
			    	else
			    	{
			    	    /*insert the new member to the proper position */
			    		int t;
			    		for(t = 0;t < this.Movie_User_list.get(row_counter).size();t ++)
			    		{
			    			if(this.Movie_User_list.get(row_counter).get(t) > userid)
			    			{
			    				this.Movie_User_list.get(row_counter).add(t, userid);
			    				this.Movie_User_Score.get(row_counter).add(t, score);
			    				break;
			    			}
			    			
			    		}
			    		if(t == this.Movie_User_list.get(row_counter).size())
			    		{
			    			this.Movie_User_list.get(row_counter).add(userid);
					    	this.Movie_User_Score.get(row_counter).add(score);
			    		}
			    	
			    	}
			    	int ind;
			    	/*If this is a new member, we add a new array lists for storing */
			    	if((ind = this.UserID.indexOf(userid)) == -1)
		    		{
		    			this.UserID.add(userid);
		    			this.User_Movie_list.add(new ArrayList<Integer>());
		    			this.User_Movie_list.get(this.User_Movie_list.size() - 1).add(count);
		    			this.User_Movie_Score.add(new ArrayList<Integer>());
		    			this.User_Movie_Score.get(this.User_Movie_Score.size() - 1).add(score);
		    			this.total_average += score;
		    			this.total_num += 1;
		    		}
			    	/*If this is an old member, we add this element to an existing list */
		    		else
		    		{
		    			this.User_Movie_list.get(ind).add(count);
		    			this.User_Movie_Score.get(ind).add(score);
		    			this.total_average += score;
		    			this.total_num += 1;
		    		}
			    	counter ++;
			    }
			    row_counter ++;
				count ++;
				readert.close();
				
			}
			catch(IOException e)
			{
				count ++;
			}
			
		
		}
		this.total_average = this.total_average/this.total_num;
	}
	/*Get the average of total ratings in the corpus */
	public double get_total_average()
	{
		return this.total_average;
	}
	public int get_substring(String str,char c)
	{
		int i = 0;
		while(str.charAt(i) != c)
			i ++;
		return i;
	}
	/*Print these lists for check */
	public void print_list()
	{
		File print_file = new File("Training_Set/print_file.txt");
		try{
			print_file.createNewFile();
			FileWriter fw =  new FileWriter(print_file);
			fw.write("movie user list is" + "\n");
			for(int i = 0;i < this.Movie_User_list.size();i ++)
			{
				fw.write(String.valueOf(i));
				fw.write(":");
				for(int j = 0;j < this.Movie_User_list.get(i).size();j ++)
				{
					fw.write(this.Movie_User_list.get(i).get(j).toString());
					fw.write(" ");
				}
				fw.write("\n");
			}
			fw.write("movieid list is:\n");
			for(int n = 0;n < this.MovieID.size();n ++)
			{
				fw.write(String.valueOf(n) + ":");
				fw.write(this.MovieID.get(n).toString());
				fw.write("\n");
				
			}
			fw.write("movie score list is" + "\n");
			for(int i = 0;i < this.Movie_User_Score.size();i ++)
			{
				fw.write(String.valueOf(i));
				fw.write(":");
				for(int j = 0;j < this.Movie_User_Score.get(i).size();j ++)
				{
					fw.write(this.Movie_User_Score.get(i).get(j).toString());
					fw.write(" ");
				}
				fw.write("\n");
			}
			
			fw.write("user movie list is" + "\n");
			for(int i = 0;i < this.User_Movie_list.size();i ++)
			{
				fw.write(String.valueOf(i));
				fw.write(":");
				for(int j = 0;j < this.User_Movie_list.get(i).size();j ++)
				{
					fw.write(this.User_Movie_list.get(i).get(j).toString());
					fw.write(" ");
				}
				fw.write("\n");
			}
			fw.write("user list is:\n");
			for(int n = 0;n < this.UserID.size();n ++)
			{
				fw.write(String.valueOf(n) + ":");
				fw.write(this.UserID.get(n).toString());
				fw.write("\n");
				
			}
			fw.write("user score list is" + "\n");
			for(int i = 0;i < this.User_Movie_Score.size();i ++)
			{
				fw.write(String.valueOf(i));
				fw.write(":");
				for(int j = 0;j < this.User_Movie_Score.get(i).size();j ++)
				{
					fw.write(this.User_Movie_Score.get(i).get(j).toString());
					fw.write(" ");
				}
				fw.write("\n");
			}
			fw.close();
		}
		catch(IOException e)
		{
			System.out.println("print Error!");
		}
		
	}
	public ArrayList<ArrayList<Integer>> get_Movie_User_List()
	{
		return this.Movie_User_list;
	}
	public ArrayList<ArrayList<Integer>> get_Movie_User_Score()
	{
		return this.Movie_User_Score;
	}
	public ArrayList<Integer> get_movieidlist()
	{
		return this.MovieID;
	}
	public ArrayList<ArrayList<Integer>> get_User_Movie_List()
	{
		return this.User_Movie_list;
	}
	public ArrayList<ArrayList<Integer>> get_User_Movie_Score()
	{
		return this.User_Movie_Score;
	}
	public ArrayList<Integer> get_Userid()
	{
		return this.UserID;
	}
	/*This main is just for checking my program, it is useless in the lab */
	public static void main(String[] args){
		
		Training_set newset = new Training_set();
		newset.Reading_Training_Set();
		newset.print_list();
		System.out.println("Success!");
	}
	
}
