package homework5;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import homework5.Training_set;
/*
 * This class is designed to implement 4 algorithms and do a experiment on the training data
 */
public class SimilarityandRating {

	private Training_set real_set;
	private int k;
	private int movieid;
	private int userid;
	private double rate;
	/*list storing k neighbors */
	private ArrayList<Integer> neighbor_list = new ArrayList<Integer>();
	/*list storing k neighbors' similarity to target user or movie */
	private ArrayList<Integer> neighbor_score = new ArrayList<Integer>();
	/*list storing k neighbors' ratings */
	private ArrayList<Integer> true_mark = new ArrayList<Integer>();
	/*list storing k neighbors' similarities to target user in custom algorithm  */
	private ArrayList<Double> custom_neighbor_score = new ArrayList<Double>();
	private double test1 = 0;
	private double test2 = 0;
	private double test3 = 0;
	/*Constructor */
	public SimilarityandRating(int k_val,int target_movieid,int target_userid)
	{
		this.real_set = new Training_set();
		this.real_set.Reading_Training_Set();
		this.k = k_val;
		this.movieid = target_movieid;
		this.userid = target_userid;
		this.rate = 0;
		
	}
	public double gettest1()
	{
		return this.test1;
		
	}
	public double gettest2()
	{
		return this.test2;
		
	}
	public double gettest3()
	{
		return this.test3;
		
	}
	public void set_movieid(int m_id)
	{
		this.movieid = m_id;
	}
	public void set_userid(int u_id)
	{
		this.userid = u_id;
	}
	public void set_rate(double ra)
	{
		this.rate = ra;
	}
	public ArrayList<Double> get_custom_neighbor_score()
	{
		return this.custom_neighbor_score;
	}
	public ArrayList<Integer> get_true_mark()
	{
		return this.true_mark;
	}
	public ArrayList<Integer> get_neighbor_list()
	{
		return this.neighbor_list;
	}
	public ArrayList<Integer> get_neighbor_score()
	{
		return this.neighbor_score;
	}
	public Training_set get_train_set()
	{
		return this.real_set;
	}
	public double get_rate()
	{
		return this.rate;
	}
	/* movie-movie similarity algorithm(The second algorithm implementation) */
	public void Calculate_movie_k_neighbor()
	{
		int ID_inlist = this.real_set.get_movieidlist().indexOf(this.movieid);
		int sign = this.real_set.get_Userid().indexOf(this.userid);
	    int size = this.real_set.get_User_Movie_List().get(sign).size();
	    /*If this user has rated less than k movies, we simply use these movies as neighbors */
		if( size <= this.k)
		{
			for(int i = 0;i < size;i ++)
			{
				rate += this.real_set.get_User_Movie_Score().get(sign).get(i);
			}
			rate = rate/size;
		}
		/*If more than k movies , we have to select k neighbors */
		else
		{
			for(int i = 0;i < size;i ++)
			{
				int simi;
				int true_user_num = this.real_set.get_User_Movie_List().get(sign).get(i);
				int row_num = this.real_set.get_movieidlist().indexOf(true_user_num);
				if(row_num != ID_inlist)
				{
					/*Calculate similarity */
					simi = Calculate_Movieid_Similarity(ID_inlist,row_num);
					/*if no element in the list, we add it */
					if(this.neighbor_score.size() == 0)
					{
					
						this.neighbor_score.add(simi);
				        this.true_mark.add(this.real_set.get_User_Movie_Score().get(sign).get(i));
					}
					else
					{
					
						int j;
						/*insert the element and only considering the maximum k similarities  */
						for(j = 0;j < this.neighbor_score.size();j ++)
						{
							if(this.neighbor_score.get(j) <= simi)
							{
								this.neighbor_score.add(j, simi);
								this.true_mark.add(j,this.real_set.get_User_Movie_Score().get(sign).get(i));
								break;
							}
						}
						if(j == this.neighbor_score.size() && j < this.k)
						{
							this.neighbor_score.add(simi);
							this.true_mark.add(this.real_set.get_User_Movie_Score().get(sign).get(i));
						}
						if(this.neighbor_score.size() > this.k)
						{
							this.neighbor_score.remove(this.neighbor_score.size()-1);
							this.true_mark.remove(this.true_mark.size()-1);
						}
					}
				}
			}
			/*This is the rating part, simply get the mean of k neighbor ratings */
			for(int m = 0;m < this.true_mark.size();m ++)
			{
				this.rate += this.true_mark.get(m);
			}
			this.rate = this.rate/this.true_mark.size();
		}
			
	}
	/*Calculate movie-movie similarity for the second algorithm*/
	public int Calculate_Movieid_Similarity(int ID1, int ID2)//ID1 and ID2 is the index in the Movie_User_list
	{
		int result = 0;
		int ptr1 = 0,ptr2 = 0;
	
		ArrayList<Integer> list1 = this.real_set.get_Movie_User_List().get(ID1);
		ArrayList<Integer> list2 = this.real_set.get_Movie_User_List().get(ID2);
		ArrayList<Integer> Score1 = this.real_set.get_Movie_User_Score().get(ID1);
		ArrayList<Integer> Score2 = this.real_set.get_Movie_User_Score().get(ID2);
		while((ptr1 < list1.size()) && (ptr2 < list2.size()))
		{
		
			if((list1.get(ptr1) - list2.get(ptr2)) == 0)
			{
		        /*Calculate dot product and add */
				result += Score1.get(ptr1) * Score2.get(ptr2);
				ptr1 ++;
				ptr2 ++;
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) < 0)
			{
				ptr1 ++;
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) > 0)
			{
				ptr2 ++;
			}
		
		}
		return result;
	}
	/* user-user similarity algorithm (The first algorithm implementation) */
	public void Calculate_user_k_neighbor()
	{
		int ID_inlist = this.real_set.get_Userid().indexOf(this.userid);
	
		int sign = this.real_set.get_movieidlist().indexOf(this.movieid);
		int size = this.real_set.get_Movie_User_List().get(sign).size();
		/*If this movie is rated less than k users, we simply use these as neighbors */
		if( size <= this.k)
		{
			for(int i = 0;i < size;i ++)
			{
				rate += this.real_set.get_Movie_User_Score().get(sign).get(i);
			}
			rate = rate/size;
		}
		else
		{
			for(int i = 0;i < size;i ++)
			{
				int simi;
				int true_user_num = this.real_set.get_Movie_User_List().get(sign).get(i);
				int row_num = this.real_set.get_Userid().indexOf(true_user_num);
				if(row_num != ID_inlist)
				{
				    /*Calculate user user similarity */
					simi = Calculate_Userid_Similarity(ID_inlist,row_num);
				
					if(this.neighbor_score.size() == 0)
					{
				
						this.neighbor_score.add(simi);
			
						this.true_mark.add(this.real_set.get_Movie_User_Score().get(sign).get(i));
					}
					else
					{
				
						int j;
						/*Maintain the most k neighbors and their ratings and similarities */
						for(j = 0;j < this.neighbor_score.size();j ++)
						{
							if(this.neighbor_score.get(j) < simi)
							{
								this.neighbor_score.add(j, simi);
								this.true_mark.add(j,this.real_set.get_Movie_User_Score().get(sign).get(i));
								break;
							}
						}
						if(j == this.neighbor_score.size() && j < this.k)
						{
							this.neighbor_score.add(simi);
							this.true_mark.add(this.real_set.get_Movie_User_Score().get(sign).get(i));
						}
						if(this.neighbor_score.size() > this.k)
						{
							this.neighbor_score.remove(this.neighbor_score.size()-1);
							this.true_mark.remove(this.true_mark.size()-1);
						}
					}
				}
			}
			/*This is the rating part, we use mean method */
			for(int m = 0;m < this.true_mark.size();m ++)
			{
				this.rate += this.true_mark.get(m);
			}
			this.rate = this.rate/this.true_mark.size();
		}
		
	}
	/*Calculate user-user similarity */
	public int Calculate_Userid_Similarity(int ID1, int ID2)//ID1 and ID2 is the index in the Movie_User_list
	{
		int result = 0;
		int ptr1 = 0,ptr2 = 0;
		ArrayList<Integer> list1 = this.real_set.get_User_Movie_List().get(ID1);
		ArrayList<Integer> list2 = this.real_set.get_User_Movie_List().get(ID2);
		ArrayList<Integer> Score1 = this.real_set.get_User_Movie_Score().get(ID1);
		ArrayList<Integer> Score2 = this.real_set.get_User_Movie_Score().get(ID2);
	
		while((ptr1 < list1.size()) && (ptr2 < list2.size()))
		{
		
			if((list1.get(ptr1) - list2.get(ptr2)) == 0)
			{
		
				result += Score1.get(ptr1) * Score2.get(ptr2);
		
				ptr1 ++;
				ptr2 ++;
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) < 0)
			{
	
				ptr1 ++;
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) > 0)
			{
		
				ptr2 ++;
			}
		
		}
		return result;
	}
	/* user-user similarity + normalization algorithm (Third algorithm implementation)*/
	public void Calculate_user_k_normalize_neighbor()
	{
		int ID_inlist = this.real_set.get_Userid().indexOf(this.userid);
		
		int sign = this.real_set.get_movieidlist().indexOf(this.movieid);
		int size = this.real_set.get_Movie_User_List().get(sign).size();
		if( size < this.k)
		{
				
			for(int i = 0;i < size;i ++)
			{
				int tmp_usrid = this.real_set.get_Movie_User_List().get(sign).get(i);
				int sig = this.real_set.get_Userid().indexOf(tmp_usrid);
				double usr_average = 0;
				for(int j = 0;j < this.real_set.get_User_Movie_Score().get(sig).size();j ++)
				{
					usr_average += this.real_set.get_User_Movie_Score().get(sig).get(j);					}
					usr_average = usr_average/this.real_set.get_User_Movie_Score().get(sig).size();
					rate += this.real_set.get_Movie_User_Score().get(sign).get(i) - usr_average;
				}
			    rate = (rate/size) + this.get_train_set().get_total_average();
			}
			else
			{  
				
				for(int i = 0;i < size;i ++)
				{
					int simi;
					int true_user_num = this.real_set.get_Movie_User_List().get(sign).get(i);
					int row_num = this.real_set.get_Userid().indexOf(true_user_num);
					
					if(row_num != ID_inlist)
					{
					    /*Calculate the dot product similarity */
						simi = Calculate_Userid_normalize_Similarity(ID_inlist,row_num);
					
						if(this.neighbor_score.size() == 0)
						{
					
							this.neighbor_score.add(simi);
				
							this.neighbor_list.add(row_num);
							this.true_mark.add(this.real_set.get_Movie_User_Score().get(sign).get(i));
						}
						else
						{
				
							int j;
							/*Maintain the maximum k similarity neighbors */
							for(j = 0;j < this.neighbor_score.size();j ++)
							{
								if(this.neighbor_score.get(j) < simi)
								{
									this.neighbor_list.add(j,row_num);
									this.neighbor_score.add(j, simi);
									this.true_mark.add(j,this.real_set.get_Movie_User_Score().get(sign).get(i));
									break;
								}
							}
							if(j == this.neighbor_score.size() && j < this.k)
							{
								this.neighbor_list.add(row_num);
								this.neighbor_score.add(simi);
								this.true_mark.add(this.real_set.get_Movie_User_Score().get(sign).get(i));
							}
							if(this.neighbor_score.size() > this.k)
							{
								this.neighbor_list.remove(this.neighbor_list.size()-1);
								this.neighbor_score.remove(this.neighbor_score.size()-1);
								this.true_mark.remove(this.true_mark.size()-1);
							}
						}
					}
				}
				double score_sum = 0;
				/*This is the rating part. Details and formula are illustrated in the report */
				for(int n = 0;n < this.neighbor_score.size();n ++)
				{
					score_sum += this.neighbor_score.get(n);
				}
				for(int m = 0;m < this.true_mark.size();m ++)
				{
					
					int sig = this.neighbor_list.get(m);
					double usr_average = 0;
					for(int j = 0;j < this.real_set.get_User_Movie_Score().get(sig).size();j ++)
					{
						usr_average += this.real_set.get_User_Movie_Score().get(sig).get(j);
					}
					usr_average = usr_average/this.real_set.get_User_Movie_Score().get(sig).size();
					if(score_sum == 0)
					{
						this.rate =this.rate + (this.true_mark.get(m) - usr_average) * ((double)this.neighbor_score.get(m)/this.neighbor_score.size());
					}
					else
					{
						this.rate =this.rate + (this.true_mark.get(m) - usr_average) * ((double)this.neighbor_score.get(m)/score_sum);
					}
					
					
					
				}
				this.rate = (this.rate) + this.real_set.get_total_average();
				/*If the rate is more than 5, we put it to be 5 */
				if((this.rate - 5) > 0)
				{
					this.rate = 5;
				}
				/*If less than 1, we put it to be 1 */
				if((this.rate - 1) < 0)
				{
					this.rate = 1;
				}
			}
		     
	}
	/*This is the similarity calculating for third algorithm(also dot product) */
	public int Calculate_Userid_normalize_Similarity(int ID1, int ID2)//ID1 and ID2 is the index in the Movie_User_list
	{
		int result = 0;
		int ptr1 = 0,ptr2 = 0;
		ArrayList<Integer> list1 = this.real_set.get_User_Movie_List().get(ID1);
		ArrayList<Integer> list2 = this.real_set.get_User_Movie_List().get(ID2);
		ArrayList<Integer> Score1 = this.real_set.get_User_Movie_Score().get(ID1);
		ArrayList<Integer> Score2 = this.real_set.get_User_Movie_Score().get(ID2);

		while((ptr1 < list1.size()) && (ptr2 < list2.size()))
		{
		
			if((list1.get(ptr1) - list2.get(ptr2)) == 0)
			{
		
				result += Score1.get(ptr1) * Score2.get(ptr2);
		
				ptr1 ++;
				ptr2 ++;
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) < 0)
			{

				ptr1 ++;
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) > 0)
			{
			
				ptr2 ++;
			}
		
		}
		return result;
	}
	/*custom algorithm(fourth algorithm Implementation) */
	public void Calculate_user_custom_neighbor()
	{
		int ID_inlist = this.real_set.get_Userid().indexOf(this.userid);

		int sign = this.real_set.get_movieidlist().indexOf(this.movieid);
		int size = this.real_set.get_Movie_User_List().get(sign).size();
		
			for(int i = 0;i < size;i ++)
			{
				double simi;
				int true_user_num = this.real_set.get_Movie_User_List().get(sign).get(i);
				int row_num = this.real_set.get_Userid().indexOf(true_user_num);
				if(row_num != ID_inlist)
				{
				    /*Calculate the cosine similarity of two vectors */
					simi = Calculate_Userid_custom_Similarity(ID_inlist,row_num);
			
					if(this.custom_neighbor_score.size() == 0)
					{
			
						this.custom_neighbor_score.add(simi);
			
						this.neighbor_list.add(row_num);
						this.true_mark.add(this.real_set.get_Movie_User_Score().get(sign).get(i));
					}
					else
					{
			
						int j;
						/*Maintain the k neighbors and their ratings */
						for(j = 0;j < this.custom_neighbor_score.size();j ++)
						{
							if(this.custom_neighbor_score.get(j) < simi)
							{
								this.custom_neighbor_score.add(j, simi);
								this.neighbor_list.add(j,row_num);
								this.true_mark.add(j,this.real_set.get_Movie_User_Score().get(sign).get(i));
								break;
							}
						}
						if(j == this.custom_neighbor_score.size() && j < this.k)
						{
							this.custom_neighbor_score.add(simi);
							this.neighbor_list.add(row_num);
							this.true_mark.add(this.real_set.get_Movie_User_Score().get(sign).get(i));
						}
						if(this.custom_neighbor_score.size() > this.k)
						{
							this.neighbor_list.remove(this.neighbor_list.size()-1);
							this.custom_neighbor_score.remove(this.custom_neighbor_score.size()-1);
							this.true_mark.remove(this.true_mark.size()-1);
						}
					}
				}
			}
            /*This is rating part. This part is similar to the third algorithm */
			double score_sum = 0;
			for(int n = 0;n < this.custom_neighbor_score.size();n ++)
			{
				score_sum += this.custom_neighbor_score.get(n);
			}
			for(int m = 0;m < this.true_mark.size();m ++)
			{
				
				int sig = this.neighbor_list.get(m);
				double usr_average = 0;
				for(int j = 0;j < this.real_set.get_User_Movie_Score().get(sig).size();j ++)
				{
					usr_average += this.real_set.get_User_Movie_Score().get(sig).get(j);
				}
				usr_average = usr_average/this.real_set.get_User_Movie_Score().get(sig).size();
				if(score_sum == 0)
				{
					this.rate =this.rate + (this.true_mark.get(m) - usr_average) * (this.custom_neighbor_score.get(m)/this.true_mark.size());
				}
				else
				{
					this.rate =this.rate + (this.true_mark.get(m) - usr_average) * (this.custom_neighbor_score.get(m)/score_sum);
				}
				
		
			}
			
			this.rate = 1.1 * this.rate + this.real_set.get_total_average();
			if((this.rate - 5) > 0)
			{
				this.rate = 5;
			}
			if((this.rate - 1) < 0)
			{
				this.rate = 1;
			}
	
	}
	/*Calculate the cosine similarity of two users */
	public double Calculate_Userid_custom_Similarity(int ID1, int ID2)//ID1 and ID2 is the index in the Movie_User_list
	{
		double result = 0;
		int ptr1 = 0,ptr2 = 0;
		ArrayList<Integer> list1 = this.real_set.get_User_Movie_List().get(ID1);
		ArrayList<Integer> list2 = this.real_set.get_User_Movie_List().get(ID2);
		ArrayList<Integer> Score1 = this.real_set.get_User_Movie_Score().get(ID1);
		ArrayList<Integer> Score2 = this.real_set.get_User_Movie_Score().get(ID2);
	
		double list1_sum = 0;
		double list2_sum = 0;
		double mean_list1 = 0;
		double mean_list2 = 0;
		double mean_number = 0;
		
		double sum_mean = 0;
	
		while((ptr1 < list1.size()) && (ptr2 < list2.size()))
		{
	
			if((list1.get(ptr1) - list2.get(ptr2)) == 0)
			{
	
				result += Score1.get(ptr1) * Score2.get(ptr2);
				list1_sum += (Score1.get(ptr1))* (Score1.get(ptr1) );
				list2_sum += (Score2.get(ptr2)) * (Score2.get(ptr2) );
	
				ptr1 ++;
				ptr2 ++;
				
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) < 0)
			{
		
				list1_sum += Score1.get(ptr1)* Score1.get(ptr1);
				ptr1 ++;
			}
			else if((list1.get(ptr1) - list2.get(ptr2)) > 0)
			{
			
				list2_sum += Score2.get(ptr2) * Score2.get(ptr2);
				ptr2 ++;
			}
	
		}
		if(result == 0)
		{
			return 0;
		}
		result = result/Math.sqrt(list1_sum * list2_sum);
		return result;
		
		
	}
	/*This is the executions point. In this main, I have called and executed four algorithms requested in the homework.
	 * The order is same to the order described in the report.In the code, I use the small query file in the code since it will not
	 * consume much time. I also test my algorithms on the large set. You can modify the target file and test it as you wish. 
	 * I will display time for 4 algorithms in stdout. The rating result of four algorithms will store in respectively
	 *  (1) /query/result_user_mean.txt (2) /query/result_movie_mean.txt (3)/query/result_user_norm.txt
	 *  (4) /query/result_user_custom.txt
	 * 
	 */
	public static void main(String[] args){
		/*This is the execution of First algorithm(user-user similarity) */
		File query_file = new File("query/queries-small.txt");
		File result_file = new File("query/result_user_mean.txt");
		int q_movieid = 0;
		int q_userid = 0;
		long time_begin = System.currentTimeMillis();
		try{
			result_file.createNewFile();
			FileWriter fw =  new FileWriter(result_file);
			
			FileInputStream fist = new FileInputStream(query_file);     
		    InputStreamReader isrt = new InputStreamReader(fist, "UTF-8");
		    BufferedReader readert = new BufferedReader(isrt);
	
			String str;
			int i = 0;
			SimilarityandRating my_simi = new SimilarityandRating(40,q_movieid,q_userid);
		    while((str = readert.readLine()) != null)
		    {
		    	if(str.charAt(str.length()-1) == ':')
		    	{
		   
		    		fw.write(str);
		    		fw.write("\n");
		    		q_movieid = Integer.valueOf(str.substring(0, str.length()-1));
		    		q_userid = Integer.valueOf(readert.readLine());
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_user_k_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    		
		    	}
		    	else
		    	{
		    		q_userid = Integer.valueOf(str);
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_user_k_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    	}
		    	my_simi.set_rate(0);
		    	my_simi.get_true_mark().clear();
		    	my_simi.get_neighbor_score().clear();
		    	my_simi.get_neighbor_list().clear();
		    	i ++;
		   
		    }
		    readert.close();
		    fw.close();
		    
		}
		catch(IOException e)
		{
			System.out.println("Error in file operation");
		}
		long time_end = System.currentTimeMillis();
	    long time_consumed = time_end - time_begin;
	    System.out.println("Run time user_mean is :" + time_consumed);
		/*This is the execution of Second Algorithm(Movie-Movie similarity) */
		File result_file2 = new File("query/result_movie_mean.txt");
		q_movieid = 0;
	    q_userid = 0;
		
	    long time_begin2 = System.currentTimeMillis();
		try{
			result_file2.createNewFile();
			FileWriter fw =  new FileWriter(result_file2);
			
			FileInputStream fist = new FileInputStream(query_file);     
		    InputStreamReader isrt = new InputStreamReader(fist, "UTF-8");
		    BufferedReader readert = new BufferedReader(isrt);
		
			String str;
			int i = 0;
			SimilarityandRating my_simi = new SimilarityandRating(80,q_movieid,q_userid);
		    while((str = readert.readLine()) != null)
		    {
		    	if(str.charAt(str.length()-1) == ':')
		    	{
		    	
		    		
		    		fw.write(str);
		    		fw.write("\n");
		    		q_movieid = Integer.valueOf(str.substring(0, str.length()-1));
		    		q_userid = Integer.valueOf(readert.readLine());
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_movie_k_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    		
		    	}
		    	else
		    	{
		    		q_userid = Integer.valueOf(str);
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_movie_k_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    	}
		    	my_simi.set_rate(0);
		    	my_simi.get_true_mark().clear();
		    	my_simi.get_neighbor_score().clear();
		    	my_simi.get_neighbor_list().clear();
		    	i ++;
		 
		    }
		    readert.close();
		    fw.close();
		    
		}
		catch(IOException e)
		{
			System.out.println("Error in file operation");
		}
		long time_end2 = System.currentTimeMillis();
	    long time_consumed2 = time_end2 - time_begin2;
	    System.out.println("Run time movie_mean is :" + time_consumed2);
		/*This is the execution of third algorithm (user-user similarity + normalization) */
		File result_file3 = new File("query/result_user_norm.txt");
		q_movieid = 0;
		q_userid = 0;
		
		long time_begin3 = System.currentTimeMillis();
		try{
			result_file3.createNewFile();
			FileWriter fw =  new FileWriter(result_file3);
			
			FileInputStream fist = new FileInputStream(query_file);     
		    InputStreamReader isrt = new InputStreamReader(fist, "UTF-8");
		    BufferedReader readert = new BufferedReader(isrt);
	
			String str;
			int i = 0;
			SimilarityandRating my_simi = new SimilarityandRating(60,q_movieid,q_userid);
		    while((str = readert.readLine()) != null)
		    {
		    	if(str.charAt(str.length()-1) == ':')
		    	{
		    		
		    		fw.write(str);
		    		fw.write("\n");
		    		q_movieid = Integer.valueOf(str.substring(0, str.length()-1));
		    		q_userid = Integer.valueOf(readert.readLine());
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_user_k_normalize_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    		
		    	}
		    	else
		    	{
		    		q_userid = Integer.valueOf(str);
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_user_k_normalize_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    	}
		    	my_simi.set_rate(0);
		    	my_simi.get_true_mark().clear();
		    	my_simi.get_neighbor_score().clear();
		    	my_simi.get_neighbor_list().clear();
		    	i ++;
		  
		    }
		    readert.close();
		    fw.close();

		}
		catch(IOException e)
		{
			System.out.println("Error in file operation");
		}
		long time_end3 = System.currentTimeMillis();
	    long time_consumed3 = time_end3 - time_begin3;
	    System.out.println("Run time norm is :" + time_consumed3);
		/* This is execution of the fourth algorithm(Custom Algorithm) */
		File result_file4 = new File("query/result_user_custom.txt");
		q_movieid = 0;
		q_userid = 0;
		
		long time_begin4 = System.currentTimeMillis();
		try{
			result_file4.createNewFile();
			FileWriter fw =  new FileWriter(result_file4);
			
			FileInputStream fist = new FileInputStream(query_file);     
		    InputStreamReader isrt = new InputStreamReader(fist, "UTF-8");
		    BufferedReader readert = new BufferedReader(isrt);
		
			String str;
			int i = 0;
			SimilarityandRating my_simi = new SimilarityandRating(120,q_movieid,q_userid);
		    while((str = readert.readLine()) != null)
		    {
		    	if(str.charAt(str.length()-1) == ':')
		    	{
		    		
		    		fw.write(str);
		    		fw.write("\n");
		    		q_movieid = Integer.valueOf(str.substring(0, str.length()-1));
		    		q_userid = Integer.valueOf(readert.readLine());
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_user_custom_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    		
		    	}
		    	else
		    	{
		    		q_userid = Integer.valueOf(str);
		    		
		    		my_simi.set_movieid(q_movieid);
		    		my_simi.set_userid(q_userid);
		    		my_simi.Calculate_user_custom_neighbor();
		    		fw.write(String.valueOf(my_simi.get_rate()));
		    		fw.write("\n");
		    	}
		    	my_simi.set_rate(0);
		    	my_simi.get_true_mark().clear();
		    	my_simi.get_neighbor_score().clear();
		    	my_simi.get_neighbor_list().clear();
		    	my_simi.get_custom_neighbor_score().clear();
		    	i ++;
	
		    }
		    readert.close();
		    fw.close();
		 
		}
		catch(IOException e)
		{
			System.out.println("Error in file operation");
		}
		long time_end4 = System.currentTimeMillis();
	    long time_consumed4 = time_end4 - time_begin4;
	    System.out.println("Run time costom is :" + time_consumed4);
	}
}
