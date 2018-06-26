package myOwnSQL;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import javax.sql.rowset.*;
import com.sun.rowset.JdbcRowSetImpl;
import javax.swing.table.*;
import java.net.URL;

/**
 * MyOwnSQL is a GUI MySQL client that accesses MySQL server.
 * The program supports most of frequently used table-level statements:  DROP/CREATE/ALTER TABLE
 * and row-level statements: SELECT/INSERT/UPDATE/DELETE.
 * IMPORTANT NOTES:
 * - The program assumes that the MySQL server is running as localhost on the default port number 3306.
 * - An appropriate JDBC driver must be installed. 
 * For JDK 8 and lower, you can copy the JDBC driver's JAR-file into JDK's extension directory at "<JAVA_HOME>\jre\lib\ext".
 * You can also include driver's JAR-file in the environment variable CLASSPATH.
 * Optionally you can run the program at CMD shell with the java's command-line option -cp:
 * java -cp "path to"\MyOwnSQL.jar;"path to"\"driver super-long filename".jar myOwnSQL.MyOwnSQL .
 */
@SuppressWarnings("serial")
// A Swing GUI program inherits the top-level container javax.swing.JFrame
public class MyOwnSQL extends JFrame {
	
	// Declaration of the GUI components and class variables 
	private JPanel northPanel,centerPanel;
	private JLabel logoLabel,databaseLabel,userLabel;
	private JButton btn1,btn2,btn3;
	private String userLabelName,userName,userPassword,databaseLabelName,databaseName,databaseURL;
	private String imgLogoFilename="logo.jpg";
	private String imgExeFilename="exe.jpg";
	private String imgDBFilename="db.jpg";
	private String imgUserFilename="user.jpg";
	private StringBuilder statement=null;
	private boolean userCheck,passwordCheck;
	private JScrollPane scrollPane1,scrollPane2;
	private JTextArea statementBuildingArea;
	private JTable responseTable; 
	private MyOwnSQLTableModel responseModel=null;
	private JdbcRowSet rowSet=null;
	
	/**
	 *  Public constructor to setup the GUI components.
	 */
	public MyOwnSQL ()	{
		
		this.setTitle("MyOwnSQL");
		Container cp=this.getContentPane();
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1400, 800);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
		northPanel=new JPanel();
		northPanel.setLayout(new GridLayout(2,1,5,0));
		northPanel.setPreferredSize(new Dimension(1380,200));
		northPanel.setBackground(new Color(81,250,250,55));
		
		logoLabel=new JLabel();
		URL iconLogoURL=getClass().getClassLoader().getResource(imgLogoFilename);
		ImageIcon iconLogo=new ImageIcon(iconLogoURL);
		logoLabel.setIcon(iconLogo);
		logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		JPanel actionPanel=new JPanel();
		actionPanel.setBorder(BorderFactory.createTitledBorder("Action panel"));
		
		URL iconUserURL=getClass().getClassLoader().getResource(imgUserFilename);
		ImageIcon iconUser=new ImageIcon(iconUserURL);
		btn1=new JButton("Set user",iconUser);
		btn1.setPreferredSize(new Dimension(180,60));
		btn1.setHorizontalAlignment(SwingConstants.LEFT);
		btn1.setHorizontalTextPosition(SwingConstants.RIGHT);
		btn1.setIconTextGap(20);
		btn1.setMnemonic('u');
		
		// Event handler for button "Set user"
		btn1.addActionListener(new ActionListener() {
	
			/*
			 * Displays a dialog box
			 * and asks user for username and password
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				userCheck=false;
				passwordCheck=false;
				JDialog userAndPassword=new JDialog();
				userAndPassword.setTitle("Username & password");
				Container cP=userAndPassword.getContentPane();
				cP.setLayout(new GridLayout(2,1,5,5));
				userAndPassword.setSize(new Dimension(400,140));
				JPanel pnl1=new JPanel();
				pnl1.setLayout(new GridLayout(2,2,5,5));
				
				pnl1.add(new JLabel(" Enter username: "));
				JTextField tf=new JTextField(20);
				
				tf.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)	{
						userName=tf.getText();
						userLabel.setText(userLabelName+userName);
						userCheck=true;
					}
				});
				pnl1.add(tf);
				pnl1.add(new JLabel(" Enter password: "));
				JPasswordField pf=new JPasswordField(20);
				
				pf.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)	{
						userPassword=new String(pf.getPassword());
						passwordCheck=true;
					}
				});
				
				pnl1.add(pf);
				cP.add(pnl1);
				JPanel pnl2=new JPanel();
				JButton okBtn=new JButton(" OK ");
				okBtn.setHorizontalAlignment(SwingConstants.CENTER);
				okBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e)	{
						if(!userCheck)	{
							userName=tf.getText();
							userLabel.setText(userLabelName+userName);
						}
						if(!passwordCheck)	{
							userPassword=new String(pf.getPassword());
						}
						userAndPassword.setVisible(false);
					}
				});
				pnl2.add(okBtn);
				cP.add(pnl2);
				userAndPassword.setVisible(true);
				userAndPassword.setLocationRelativeTo(null);
			}
		});
		
		actionPanel.add(btn1);
		
		userLabelName=" User: ";
		userLabel=new JLabel(userLabelName);
		userLabel.setPreferredSize(new Dimension(300,60));
		userLabel.setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
		actionPanel.add(userLabel);
		
		URL iconDBURL=getClass().getClassLoader().getResource(imgDBFilename);
		ImageIcon iconDB=new ImageIcon(iconDBURL);
		btn2=new JButton("Set database",iconDB);
		btn2.setPreferredSize(new Dimension(180,60));
		btn2.setHorizontalAlignment(SwingConstants.LEFT);
		btn2.setHorizontalTextPosition(SwingConstants.RIGHT);
		btn2.setIconTextGap(10);
		btn2.setMnemonic(KeyEvent.VK_D);
		
		// Event handler for button "Set database"
		btn2.addActionListener(new ActionListener() {
			/* The method below asks user for database name */
			@Override
			public void actionPerformed(ActionEvent e) {
				databaseName=JOptionPane.showInputDialog("Enter default database");
				databaseLabel.setText(databaseLabelName+databaseName);
				databaseURL="jdbc:mysql://localhost:3306/"+databaseName+"?useSSL=false";
			}
		});
		actionPanel.add(btn2);
		databaseLabelName=" Default database is: ";
		databaseLabel=new JLabel(databaseLabelName);
		databaseLabel.setPreferredSize(new Dimension(480,60));
		databaseLabel.setFont(new Font("Arial",Font.ROMAN_BASELINE,20));
		actionPanel.add(databaseLabel);
		
		URL iconExeURL=getClass().getClassLoader().getResource(imgExeFilename);
		ImageIcon iconExe=new ImageIcon(iconExeURL);
		btn3=new JButton("Execute",iconExe);
		btn3.setPreferredSize(new Dimension(180,60));
		btn3.setHorizontalAlignment(SwingConstants.LEFT);
		btn3.setHorizontalTextPosition(SwingConstants.RIGHT);
		btn3.setIconTextGap(20);
		btn3.setMnemonic('e');
		
		// Event handler for button "Execute"
		btn3.addActionListener(new ActionListener() {
			/* The method below identifies the statement entered in the statement area
			 * by extracting first three letters (case insensitive), initial redundant spaces are ignored.
			 * Then an integer of ascii codes is created, and at last a matching method is invoked
			 * by switch-case mechanism. 
			 */
			@Override
			public void actionPerformed(ActionEvent e)	{
				statement=new StringBuilder(statementBuildingArea.getText());
				StringBuilder statementInLowerCase=new StringBuilder(statementBuildingArea.getText().toLowerCase());
				if(statementInLowerCase.length()>3)	{
					char [] letters=new char [3];
					int counter=0;
					int i=0;
						while ((i<statementInLowerCase.length()-1)&&(counter<3))	{
								if (statementInLowerCase.codePointAt(i)!=32)	{
									letters[counter]=statementInLowerCase.charAt(i);
									counter++;
								}
								i++;
						}	
					String lettersAsString=new String(letters);
					String statementScanner=new String(""+lettersAsString.codePointAt(0)+
							lettersAsString.codePointAt(1)+lettersAsString.codePointAt(2));	
					int statementCode=Integer.parseInt(statementScanner);
					switch (statementCode)	{
					case 115101108:
						executeSelectStatement(new String(statement));break;
					case 105110115:
						executeInsertStatement(new String(statement));break;
					case 100101108:
						executeDeleteStatement(new String(statement));break;
					case 117112100:
						executeUpdateStatement(new String(statement));break;
					case 97108116:
						executeAlterStatement(new String(statement));break;
					case 99114101:
						executeCreateStatement(new String(statement));break;
					case 100114111:
						executeDropTableStatement(new String(statement));break;
					default:
						executeMessage();
					}
				}	else	{
					JOptionPane.showMessageDialog(null,"Enter correct statement",
							"Uncorrect statement",JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		
		actionPanel.add(btn3);
		
		northPanel.add(logoLabel);
		northPanel.add(actionPanel);
		
		centerPanel=new JPanel();
		centerPanel.setLayout(new GridLayout(2,1,5,0));
		
		statementBuildingArea=new JTextArea();
		statementBuildingArea.setBackground(Color.WHITE);
		statementBuildingArea.setFont(new Font("Arial",Font.ROMAN_BASELINE,15));
		
		scrollPane1=new JScrollPane(statementBuildingArea);
		scrollPane1.setBorder(BorderFactory.createTitledBorder("Statement building area"));
		scrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerPanel.add(scrollPane1);
		
		responseTable=new JTable();
		scrollPane2=new JScrollPane(responseTable);
		scrollPane2.setBorder(BorderFactory.createTitledBorder("Response area"));
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		centerPanel.add(scrollPane2);
		
		cp.add(northPanel,BorderLayout.NORTH);
		cp.add(centerPanel,BorderLayout.CENTER);	
	}
	
	/**
	 *  The private inner class models a table model
	 *  required as a constructor parameter during instantiation of
	 *   JTable class.
	 */
	private class MyOwnSQLTableModel extends AbstractTableModel {
		private ResultSetMetaData rsetMD;
		private String [] colNames;
		private int numCols,numRows;
		private String [][] dataValues;
		
			@Override
			public String getColumnName (int column)	{
				return colNames[column];
			}
			
			/**
			 * Extracts the number of rows.
			 * @return The number of rows.
			 */
			public int getRowCount () {
				return numRows;
			}
			
			/**
			 * Extracts the number of columns.
			 * @return The number of columns.
			 */
			public int getColumnCount () {
				return numCols;
			}
			
			/**
			 * Extracts String values of all cells of given row number and column number.
			 * @param row The row order number.
			 * @param col The column order number.
			 * @return String value in the cell.
			 */
			public String getValueAt(int row,int col)	{
				return dataValues[row][col];
			}
			
			/**
			 * The constructor extracts column names from JDBCRowSet object metadata and puts
			 * them into a string array. The number of rows and the number of columns are also
			 * extracted. A two dimension string array with data values is created.
			 * @param rowSet The JDBCRowSet object containing data values.
			 * @throws SQLException
			 */
			public MyOwnSQLTableModel (JdbcRowSet rowSet)	throws SQLException {
					rsetMD=rowSet.getMetaData();
					numCols=rsetMD.getColumnCount();
					colNames=new String [numCols];
					for (int i=1;i<=numCols;i++)	{
						colNames[i-1]=rsetMD.getColumnName(i)+" ( "+rsetMD.getColumnClassName(i).substring(10)+" )";
					}
					rowSet.last();
					numRows=rowSet.getRow();
					rowSet.beforeFirst();
					dataValues=new String [numRows][numCols];
					int counter=1;
					while (rowSet.next())	{
						for (int i=1;i<=numCols;i++)	{
							dataValues[counter-1][i-1]=rowSet.getString(i);
						}
						counter++;
					}	
			}	
	}
	
	/**
	 * Executes MySQL statement of "select" type.
	 * The result rows are extracted from RowSet object and then
	 * applied into a table displayed in the response area.
	 * @param stmt The MySQL statement to be executed.
	 */
	public void executeSelectStatement(String stmt) {
		
		try	{
			rowSet=new JdbcRowSetImpl();
			rowSet.setUrl(databaseURL);
			rowSet.setUsername(userName);
			rowSet.setPassword(userPassword);
			rowSet.setCommand(stmt);
			rowSet.execute();
			
			if (!rowSet.next())	{
				responseTable.setModel(new AbstractTableModel() {
					@Override
					public String getColumnName (int column)	{
						return "";
					}
					
					public int getRowCount () {
						return 10;
					}
					
					public int getColumnCount () {
						return 10;
					}
					
					public String getValueAt(int row,int col)	{
						return "";
					}
				});
		  		JOptionPane.showMessageDialog(null,"The resultset is empty",
		  				"Empty resultset",JOptionPane.PLAIN_MESSAGE);
			}	else	{
				responseModel=new MyOwnSQLTableModel(rowSet);
				responseTable.setModel(responseModel);
			}
		}	catch (SQLException ex)	{
				showErrorMessage(ex);
		}	finally	{
				try	{
					rowSet.close();
				}	catch(SQLException ex) {
						showErrorMessage(ex);
				}
		}
	}
	
	/**
	 * Executes MySQL statement of "insert" type.
	 * A message dialog is displayed indicating number of records
	 * inserted.
	 * @param stmt The MySQL statement to be executed.
	 */
	public void executeInsertStatement(String stmt)	{
		StringBuffer tableName=extractTableName(stmt,3);
		
		try ( 
			Connection conn=DriverManager.getConnection(databaseURL, userName, userPassword);
				Statement userStmt=conn.createStatement();
			)	{
				int countInserted=userStmt.executeUpdate(stmt);
				if (countInserted==1)	{
					JOptionPane.showMessageDialog(null,countInserted+" record has been inserted into table "+
					new String(tableName),"Insert statement has been executed",JOptionPane.PLAIN_MESSAGE);
				} else	{
					JOptionPane.showMessageDialog(null,countInserted+" records have been inserted into table "+
		  				new String(tableName),"Insert statement has been executed",JOptionPane.PLAIN_MESSAGE);
				}
		}	catch(SQLException ex)	{
			showErrorMessage(ex);
		}
	}
	
	/**
	 * Executes MySQL statement of "delete" type.
	 * A message dialog is displayed indicating number of records
	 * deleted.
	 * @param stmt The MySQL statement to be executed.
	 */
	public void executeDeleteStatement(String stmt)	{
		StringBuffer tableName=extractTableName(stmt,3);
		try ( 
				Connection conn=DriverManager.getConnection(databaseURL, userName, userPassword);
				Statement userStmt=conn.createStatement();
			)	{
				int countDeleted=userStmt.executeUpdate(stmt);
				if (countDeleted==1)	{
					JOptionPane.showMessageDialog(null,countDeleted+" record has been deleted from table "+
					new String(tableName),"Delete statement has been executed",JOptionPane.PLAIN_MESSAGE);
				} else	{
					JOptionPane.showMessageDialog(null,countDeleted+" records have been deleted from table "+
		  				new String(tableName),"Delete statement has been executed",JOptionPane.PLAIN_MESSAGE);
				}
		}	catch(SQLException ex)	{
			showErrorMessage(ex);
		}
	}
	
	/**
	 * Executes MySQL statement of "update" type.
	 * A message dialog is displayed indicating number of records
	 * updated.
	 * @param stmt The MySQL statement to be executed.
	 */
	public void executeUpdateStatement(String stmt)	{
		StringBuffer tableName=extractTableName(stmt,2);
		try ( 
				Connection conn=DriverManager.getConnection(databaseURL, userName, userPassword);
				Statement userStmt=conn.createStatement();
			)	{
				int countUpdated=userStmt.executeUpdate(stmt);
				if (countUpdated==1)	{
					JOptionPane.showMessageDialog(null,countUpdated+" record has been updated in table "+
					new String(tableName),"Update statement has been executed",JOptionPane.PLAIN_MESSAGE);
				} else	{
					JOptionPane.showMessageDialog(null,countUpdated+" records have been updated in table "+
		  				new String(tableName),"Update statement has been executed",JOptionPane.PLAIN_MESSAGE);
				}
		}	catch(SQLException ex)	{
			showErrorMessage(ex);
		}
	}
	
	/**
	 * Executes MySQL statement of "alter table" type.
	 * In case of success a message dialog is displayed.
	 * @param stmt The MySQL statement to be executed.
	 */
	public void executeAlterStatement(String stmt)	{
		try ( 
				Connection conn=DriverManager.getConnection(databaseURL, userName, userPassword);
				Statement userStmt=conn.createStatement();
			)	{
				userStmt.execute(stmt);
				StringBuffer tableName=extractTableName(stmt,3);
				JOptionPane.showMessageDialog(null,"Table "+new String(tableName)+" has been modified",
						"Alter table statement has been executed",JOptionPane.PLAIN_MESSAGE);
		}	catch(SQLException ex)	{
			showErrorMessage(ex);
		}
	}
	
	/**
	 * Executes MySQL statement of "create table" type.
	 * In case of success a message dialog is displayed.
	 * @param stmt The MySQL statement to be executed.
	 */
	public void executeCreateStatement(String stmt)	{
		try ( 
				Connection conn=DriverManager.getConnection(databaseURL, userName, userPassword);
				Statement userStmt=conn.createStatement();
			)	{
			userStmt.execute(stmt);
			StringBuffer tableName;
			if(new String(extractTableName(stmt,3)).equalsIgnoreCase("if"))	{
				tableName=extractTableName(stmt,6);
			}	else	{
				tableName=extractTableName(stmt,3);
			}
			JOptionPane.showMessageDialog(null,"Table "+new String(tableName)+" has been created",
				"Create table statement has been executed",JOptionPane.PLAIN_MESSAGE);
		}	catch(SQLException ex)	{
			showErrorMessage(ex);
		}
	}
	
	/**
	 * Executes MySQL statement of "drop table" type.
	 * In case of success a message dialog is displayed.
	 * @param stmt The MySQL statement to be executed.
	 */
	public void executeDropTableStatement(String stmt)	{
		StringBuffer tableName;
		if(new String(extractTableName(stmt,3)).equalsIgnoreCase("if"))	{
			tableName=extractTableName(stmt,5);
		}	else	{
			tableName=extractTableName(stmt,3);
		}
		try ( 
				Connection conn=DriverManager.getConnection(databaseURL, userName, userPassword);
				Statement userStmt=conn.createStatement();
			)	{
			userStmt.execute(stmt);
			JOptionPane.showMessageDialog(null,"Table "+new String(tableName)+" has been dropped",
					"Drop table statement has been executed",JOptionPane.PLAIN_MESSAGE);
		}	catch(SQLException ex)	{
			showErrorMessage(ex);
		}
	}
	
	/**
	 * Extracts  table name from MySQL statement.
	 * @param stmt	The MySQL statement.
	 * @param word	Order number of the word to be extracted.
	 * @return The extracted table name.
	 */
	public StringBuffer extractTableName(String stmt,int word)	{
		StringBuffer extractedName=new StringBuffer();
		int curWord=0;
		if (stmt.codePointAt(0)!=32)	{
			curWord=1;
		}
		for (int i=1;i<stmt.length();i++)	{
			if (curWord==word)	{
				if ((stmt.codePointAt(i)!=32)&&(stmt.codePointAt(i)!=10)&&
						(stmt.codePointAt(i)!=40)&&(stmt.codePointAt(i)!=59)&&(stmt.codePointAt(i)!=44))	{
					extractedName.append(stmt.charAt(i));
				}	else {
					return extractedName;
				}
			} else	{
				if (((stmt.codePointAt(i)!=32)&&(stmt.codePointAt(i)!=10))&&
						((stmt.codePointAt(i-1)==32)||(stmt.codePointAt(i-1)==10)))	{
					curWord++;
					if (curWord==word)	{
						extractedName.append(stmt.charAt(i));
					}
				}	
			}
		}
		return new StringBuffer("Unknown table name");
	}
	
	/**
	 * Displays an error message
	  in case of a SQLException occurency.
	 * @param ex The SQLException object.
	 */
	public void showErrorMessage (SQLException ex)	{
		responseTable.setModel(new AbstractTableModel(){
			@Override
			public String getColumnName (int column)	{
				return "Error";
			}
			public int getRowCount () {
				return 10;
			}
			public int getColumnCount () {
				return 10;
			}
			public String getValueAt(int row,int col)	{
				return "Error";
			}
		});
		JOptionPane.showMessageDialog(null,"An error has occured. Exception code is: "+ex.getErrorCode(),
				"Error",JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Displays a message in case of
	 unsupported statement occurency.
	 */
	public void executeMessage() {
		JOptionPane.showMessageDialog(null,"MyOwnSQL doesn't support statement that has been entered",
				"Unsupported statement",JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	 * Run the GUI codes in the Event-Dispatching thread for thread safety
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()	{
				new MyOwnSQL();		//Let the constructor do the job
			}
		});
	}
}
