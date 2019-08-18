package Server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.swing.text.Document;

public class InSeoul_Server_commit {

	public static void main(String[] args) throws Exception, Exception {
		BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";
        Connection con = null;
        PreparedStatement st = null;
		//run("C:\\Users\\DGCOM\\Desktop\\Seoul_hot\\Seoul_Hot\\data.csv","euc-kr");
		try
		{
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://ksun1234.cafe24.com/ksun1234?characterEncoding=UTF-8&serverTimezone=UTC","ksun1234","kwonsunjae1!");
			Statement stmt = conn.createStatement();
			br = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\DGCOM\\Desktop\\Seoul_hot\\Seoul_Hot\\data.csv"),"euc-kr"));
            while ((line = br.readLine()) != null) {
            	String[] field = line.split(cvsSplitBy);
            	 if(field[1].equals("������ȣ"))continue;
            	
                String sql = "Insert into InSeoul_upso_data values(?,?,?,?) ";
    			st = conn.prepareStatement(sql);
    			st.setString(1, field[0]);
    			st.setInt(2,  Integer.parseInt(field[1]));
    			if(!line.contains(",02-")) {
    				st.setString(4,"��ȯ��ȣ �̵��");
    			}
    			else {
    				if(line.split(",02-")[1].split(",")[0].contains("070") ||line.split(",02-")[1].split(",")[0].contains("010")||line.split(",02-")[1].split(",")[0].contains("1577") ) {
        				st.setString(4,line.split(",02-")[1].split(",")[0]);
        			}
        			else {
        				st.setString(4, "02-"+line.split(",02-")[1].split(",")[0]);
        			}
    			}
    			
    			st.setString(3, field[2]);
    			
    			System.out.println(st);
    			
    			st.executeUpdate();
    			
    			
    			
                
                
			
			
			

			
			
			
            }
            conn.close();
		}catch( ClassNotFoundException e )
        {
        System.out.println("JDBC ����̹��� �������� �ʽ��ϴ�. "+e);}
		catch( java.sql.SQLException e )
		{
       System.out.println("DB ��������"+e); 
		}           
		catch( Exception e )
		{
			System.out.println("��Ÿ ���� "+e);
		}
	}
	
	private static void run(String path, String encoding) {
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
            while ((line = br.readLine()) != null) {
                String[] field = line.split(cvsSplitBy);
                for(int i =0;i <field.length ;i++) {
                	System.out.print(field[i] );
                }
                System.out.print("\n");
                
                
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
	
}
