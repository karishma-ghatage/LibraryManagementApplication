
import java.sql.*;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Lab5 extends JFrame {
	static String MemberID;
	static String ISBN;
	static String Cindate;
	static String Coutdate;
	static Connection con;
	static JFrame f1, f2, f3;
	static JTextField ffname = new JTextField(15);
	static JTextField flname = new JTextField(15);
	static JTextField fdob = new JTextField(10);
	static JTextField fgender = new JTextField(1);
	static JTextField fmemid = new JTextField(4);
	static JTextField fmemid1 = new JTextField(4);
	static JTextField bookname = new JTextField(20);
	
	static int count;
	

	private static JComboBox displaybooks ;
	private static JComboBox choice ;
	
	private static JComboBox titles ;
	private static JComboBox authors;
	private static JComboBox authorbooks ;

	
	private static JButton Checkout = new JButton("Book Check out");

	private static JButton OK = new JButton("OK");
	private static JButton Add = new JButton("Add Member");
	private static JButton Cancel = new JButton("Cancel");
	private static JButton Cancelcheckout = new JButton("Cancel");
	private static JButton next = new JButton("Next");
	private static JButton findtitle = new JButton("Find");
	private static JButton findauthor = new JButton("Find");
	public static void main(String args[]) throws ClassNotFoundException, SQLException {
		con = null;

		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://127.0.0.1/kamughat";
		con = DriverManager.getConnection(url, "kamughat", "830677576");
		// System.out.println("URL: " + url);
		// System.out.println("Connection: " + con);
		first();
		// con.close();

		Add.addActionListener(new java.awt.event.ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				int mem1 = 0;

				String gen = null;
				boolean memflag = true;
				boolean dobflag = true;
				ResultSet rs2 = null;
				Statement stmt2 = null;
				ResultSet rs3 = null;
				Statement stmt3 = null;
				try {
					stmt2 = con.createStatement();
					stmt3 = con.createStatement();
				} catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
				}

				f1.setVisible(false);

				try {
					mem1 = Integer.parseInt(fmemid1.getText());
				} catch (NumberFormatException e2) {
					memflag = false;
				}

				dobflag = aValidDate(fdob.getText());

				

				if (memflag & dobflag & ffname.getText().length() != 0 & flname.getText().length() != 0
						& fgender.getText().length() == 1 & ffname.getText().length() < 16
						& flname.getText().length() < 16 & fmemid1.getText().length() == 4
						& (fgender.getText().equalsIgnoreCase("m") | fgender.getText().equalsIgnoreCase("f"))) {
					try {
						rs2 = stmt2.executeQuery("select * from Member where MemberID='" + mem1 + "';");
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					try {
						if (!rs2.next()) {
							if (fgender.getText().equalsIgnoreCase("m")) {
								gen = "M";
							} else if (fgender.getText().equalsIgnoreCase("f")) {
								gen = "F";
							}
							int a = stmt3.executeUpdate("insert into Member values(" + mem1 + ",'" + flname.getText()
									+ "','" + ffname.getText() + "','" + fdob.getText() + "','" + gen + "');");
							if (a != 0) {
								JOptionPane.showMessageDialog(null,
										"New member with ID " + mem1 + " is inserted in Member table");
								booklist();
							} else {
								System.out.println("unable to insert member");
							}

						} else {
							JOptionPane.showMessageDialog(null,
									"This memberID already exists.\nSelect cancel to checkout a book\nOR\ngive another memner Id to create new account");
							f1.setVisible(true);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {

					JOptionPane.showMessageDialog(null,
							"Text fields cannot be empty and should be in following format only\n"
									+ "MemberID : 4 integers only\n First and Last name : max 15 letters \n"
									+ "DOB : yyyy-MM-dd\nGender : Either M or F only");
					f1.setVisible(true);
				}
			}

		});

		Checkout.addActionListener(new java.awt.event.ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				Statement stmt1 = null, stmt = null,stmt3=null;
				ResultSet rs1, rs,rs3;
				ArrayList<String> libs = new ArrayList<String>();
				ArrayList<Integer> shelfs = new ArrayList<Integer>();
				String finalisbn = null;
				try {
					stmt1 = con.createStatement();
					stmt3 = con.createStatement();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					stmt = con.createStatement();
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				int total_copies = 0;
				int total_outBooks = 0;
				
				
				if(count==2){
					//System.out.println("book  by partial search");
					String booktitle=(String) titles.getSelectedItem();
					try {
						rs3=stmt3.executeQuery("select ISBN from Book where Title='"+booktitle+"';");
						if(rs3.next()){
							finalisbn=rs3.getString(1);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
				}else if (count==3){
					//System.out.println("book  by author search");

					String booktitle=(String) authorbooks.getSelectedItem();
					try {
						rs3=stmt3.executeQuery("select ISBN from Book where Title='"+booktitle+"';");
						if(rs3.next()){
							finalisbn=rs3.getString(1);
						}
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{

			     finalisbn = (String) displaybooks.getSelectedItem();
				}
				try {
					f2.setVisible(false);
					rs = stmt.executeQuery("Select sum(Total_Copies) as Total_Copies_both from Stored_on where ISBN='"
							+ finalisbn + "'group by ISBN;");
					if (rs.next()) {
						total_copies = rs.getInt("Total_Copies_both");

					} else {
						JOptionPane.showMessageDialog(null, " library does not currently have the book in stock");
						f3.setVisible(true);
					}
					rs1 = stmt1.executeQuery("select count(ISBN)as books_checked_out from Borrowed_By where ISBN='"
							+ finalisbn + "' and Checkin_Date='0000-00-00';");
					if (rs1.next()) {
						total_outBooks = rs1.getInt("books_checked_out");
					}
					if (total_outBooks < total_copies) {
						rs1 = stmt1.executeQuery("select * from Stored_on where ISBN='" + finalisbn + "';");
						while (rs1.next()) {
							libs.add(rs1.getString(3));
							shelfs.add(rs1.getInt(2));
						}
						if (libs.size() == 1) {
							JOptionPane.showMessageDialog(null, "Book available for checkout in '" + libs.get(0)
									+ "' Library on shelf number: " + shelfs.get(0));
							f3.setVisible(true);
						} else if (libs.size() == 2) {
							JOptionPane.showMessageDialog(null,
									"Book available for checkout in following libraries :\n'" + libs.get(0)
											+ "' Library, on shelf number: " + shelfs.get(0) + "\n'" + libs.get(1)
											+ "' Library, on shelf number: " + shelfs.get(1));
							f3.setVisible(true);

						}
					} else {
						JOptionPane.showMessageDialog(null,
								"All the copies in libraries are currently checked out");
						f3.setVisible(true);

					}

					// first();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});

		Cancelcheckout.addActionListener(new java.awt.event.ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {

				f2.setVisible(false);
				f3.setVisible(true);

			}
		});

	}

	public static void first() throws SQLException {
		// System.out.println("in the first frame");
		JPanel lineone = new JPanel();
		lineone.setLayout(new FlowLayout());
		lineone.add(new JLabel("Enter Memeber ID :"));
		lineone.add(fmemid);

		JPanel linetwo = new JPanel();
		linetwo.setLayout(new BorderLayout());
		linetwo.add(lineone, BorderLayout.CENTER);

		JPanel but = new JPanel();
		but.setLayout(new FlowLayout());
		but.add(OK);

		JPanel fmem = new JPanel();
		fmem.setLayout(new BorderLayout());
		fmem.add(linetwo, BorderLayout.NORTH);
		fmem.add(but, BorderLayout.CENTER);

		f3 = new JFrame();

		f3 = new JFrame("GUI FOR Member Login");
		f3.setSize(400, 100);
		f3.setLocationRelativeTo(null);
		f3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container contentPane = f3.getContentPane();

		contentPane.add(fmem);
		f3.setVisible(true);
		// f1.setVisible(false);
		// f2.setVisible(false);

		OK.addActionListener(new java.awt.event.ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				f3.setVisible(false);
				boolean loopflag = true;
				int memid = 0;
				// while (loopflag) {
				try {
					memid = Integer.parseInt(fmemid.getText());

					loopflag = false;

					second(memid);
				} catch (NumberFormatException e2) {
					JOptionPane.showMessageDialog(null, "Member Id can be only in integers");
					f3.setVisible(true);
					loopflag = true;
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// } // end of while for memid
			}
		});

	}

	public static void second(int memid) throws SQLException {
		Statement stmt;
		ResultSet rs;
		int response = 0;
		stmt = con.createStatement();

		rs = stmt.executeQuery("SELECT * FROM Member where MemberID = '" + memid + "';");
		if (!rs.next()) {
			response = JOptionPane.showConfirmDialog(null,
					"This MemberID does not exits.\nDo you want to create new MemberID, Click yes to enter new member Information, Click no to re-enter the Id",
					"confirm", JOptionPane.YES_NO_OPTION);

			if (response == JOptionPane.NO_OPTION) {
				f3.setVisible(true);
			} else if (response == JOptionPane.YES_OPTION) {
				JPanel line_1 = new JPanel();
				line_1.setLayout(new FlowLayout());
				line_1.add(new JLabel("Last Name: "));
				line_1.add(flname);
				line_1.add(new JLabel("First Name: "));
				line_1.add(ffname);

				JPanel line_2 = new JPanel();
				line_2.setLayout(new FlowLayout());
				line_2.add(new JLabel("DOB: "));
				line_2.add(fdob);
				line_2.add(new JLabel("Gender: "));
				line_2.add(fgender);

				JPanel first = new JPanel();
				first.setLayout(new FlowLayout());
				first.add(new JLabel("MemberID: "));
				first.add(fmemid1);

				JPanel member = new JPanel();
				member.setLayout(new GridLayout(3, 1));
				member.add(first);
				member.add(line_1);
				member.add(line_2);

				JPanel but = new JPanel();
				but.setLayout(new FlowLayout());
				but.add(Add);
				but.add(Cancel);

				JPanel fmem = new JPanel();
				fmem.setLayout(new BorderLayout());
				fmem.add(member, BorderLayout.NORTH);
				fmem.add(but, BorderLayout.CENTER);

				f1 = new JFrame();

				f1 = new JFrame("GUI FOR NEW MEMBER");
				f1.setSize(600, 190);
				f1.setLocationRelativeTo(null);
				f1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				Container contentPane = f1.getContentPane();

				contentPane.add(fmem);
				f1.setVisible(true);

				Cancel.addActionListener(new java.awt.event.ActionListener() {

					@SuppressWarnings("unchecked")
					public void actionPerformed(ActionEvent e) {

						f1.setVisible(false);
						f3.setVisible(true);

					}
				});

			} // else if (response == JOptionPane.CLOSED_OPTION) {
				// first();
				// }
		} else {
			booklist();
			// f2.setVisible(true);
		}

	}

	public static void booklist() throws SQLException {
		
		Statement stmt1,stmt2;
		ResultSet rs1,rs2;
		// int response=0;
		stmt1 = con.createStatement();
		stmt2 = con.createStatement();
		// System.out.println("in booklist");
		displaybooks = new JComboBox();
		choice = new JComboBox();
		authors = new JComboBox();
		titles = new JComboBox();
		authorbooks = new JComboBox();
		
		ArrayList<String> isbns = new ArrayList<String>();
		ArrayList<String> authorslist = new ArrayList<String>();
		rs1 = stmt1.executeQuery("Select * from Book;");
		while (rs1.next()) {
			String isbn = rs1.getString(1);
			isbns.add(isbn);
		}

		for (int a = 0; a < isbns.size(); a++) {

			displaybooks.addItem(isbns.get(a));

		}
		rs2 = stmt2.executeQuery("Select * from Author;");
		while (rs2.next()) {
			String fullname = rs2.getString(2);
			fullname=fullname.concat(" ");
			fullname=fullname.concat(rs2.getString(3));
			authorslist.add(fullname);
		}
		

		for (int a = 0; a < authorslist.size(); a++) {

			authors.addItem(authorslist.get(a));

		}
		
		
		
		JPanel topchoice = new JPanel();
		topchoice.setLayout(new BorderLayout());
		topchoice.add(new JLabel ("Select a method to choose a book and then click on next"),BorderLayout.NORTH);
		choice.addItem("ISBN");
		choice.addItem("Book Name");
		choice.addItem("Author");

		topchoice.add(choice,BorderLayout.CENTER);
		JScrollPane scroll1 = new JScrollPane(choice);
		scroll1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		topchoice.add(scroll1);
		
		JPanel one = new JPanel();
		one.setLayout(new FlowLayout());
		one.add(next);

		JPanel two = new JPanel();
		two.setLayout(new BorderLayout());
		two.add(topchoice, BorderLayout.NORTH);
		two.add(one, BorderLayout.CENTER);
		
		JPanel three = new JPanel();
		three.setLayout(new BorderLayout());
		three.add(two, BorderLayout.CENTER);
		
		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BorderLayout());
		displayPanel.add(new JLabel("Select from the List of Books"), BorderLayout.NORTH);
		displayPanel.add(displaybooks, BorderLayout.CENTER);

		JScrollPane scroll = new JScrollPane(displaybooks);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		displayPanel.add(scroll);

		JPanel but = new JPanel();
		but.setLayout(new FlowLayout());
		but.add(Checkout);
		but.add(Cancelcheckout);

		
		JPanel four = new JPanel();
		four.setLayout(new BorderLayout());
		four.add(three, BorderLayout.NORTH);
		four.add(displayPanel, BorderLayout.CENTER);
		
		JPanel but1 = new JPanel();
		but1.setLayout(new FlowLayout());
		but1.add(findtitle);
		
		JPanel but2 = new JPanel();
		but2.setLayout(new FlowLayout());
		but2.add(findauthor);
		
		JPanel temp= new JPanel();
		temp.setLayout(new FlowLayout());
		temp.add(bookname);
		temp.add(but1);
		temp.add(titles);
		JScrollPane scroll2 = new JScrollPane(titles);
		scroll2.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		temp.add(scroll2);
		
		JPanel five = new JPanel();
		five.setLayout(new BorderLayout());
		five.add(new JLabel("Enter a partial name for Book search"),BorderLayout.NORTH);
		five.add(temp,BorderLayout.CENTER);
	
		JPanel temp1= new JPanel();
		temp1.setLayout(new FlowLayout());
		temp1.add(authors);
		JScrollPane scroll4 = new JScrollPane(authors);
		scroll4.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		temp1.add(scroll4);
		temp1.add(but2);
		temp1.add(authorbooks);
		JScrollPane scroll3 = new JScrollPane(authorbooks);
		scroll3.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		temp1.add(scroll3);
		
		JPanel six = new JPanel();
		six.setLayout(new BorderLayout());
		six.add(new JLabel("Select from the list of authors"),BorderLayout.NORTH);
		six.add(temp1,BorderLayout.CENTER);
		
		
		JPanel seven = new JPanel();
		seven.setLayout(new BorderLayout());
		seven.add(four, BorderLayout.NORTH);
		seven.add(five, BorderLayout.CENTER);
		seven.add(six, BorderLayout.SOUTH);
		
		JPanel bookfin = new JPanel();
		bookfin.setLayout(new BorderLayout());
		bookfin.add(seven, BorderLayout.NORTH);
		bookfin.add(but, BorderLayout.CENTER);
	
		
	
		

		f2 = new JFrame();

		f2 = new JFrame("GUI for book checkout");
		f2.setSize(700, 350);
		f2.setLocationRelativeTo(null);
		f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Container contentPane = f2.getContentPane();

		contentPane.add(bookfin);
		f2.setVisible(true);
		
		
		authors.setEnabled(false);
		authorbooks.setEnabled(false);
		displaybooks.setEnabled(false);
		titles.setEnabled(false);
		findtitle.setEnabled(false);
		findauthor.setEnabled(false);
		Checkout.setEnabled(false);
		
		
		next.addActionListener(new java.awt.event.ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {

				if(choice.getSelectedItem().equals("ISBN")){
					authors.setEnabled(false);
					authorbooks.setEnabled(false);
					displaybooks.setEnabled(true);
					titles.setEnabled(false);	
					Checkout.setEnabled(true);
					findtitle.setEnabled(false);
					findauthor.setEnabled(false);
				}else if(choice.getSelectedItem().equals("Book Name")){
					authors.setEnabled(false);
					authorbooks.setEnabled(false);
					displaybooks.setEnabled(false);
					titles.setEnabled(false);
					findtitle.setEnabled(true);
					findauthor.setEnabled(false);
					Checkout.setEnabled(false);
					
					}else if(choice.getSelectedItem().equals("Author")){
						authors.setEnabled(true);
						authorbooks.setEnabled(false);
						displaybooks.setEnabled(false);
						titles.setEnabled(false);
						findtitle.setEnabled(false);
						findauthor.setEnabled(true);
						Checkout.setEnabled(false);
				}

			}
		});

		findtitle.addActionListener(new java.awt.event.ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {

				ResultSet rs = null ;
				Statement stmt = null ;
				ArrayList<String> titlelist = new ArrayList<String>();
				titles.removeAllItems();
				int x=0;
				try {
					stmt = con.createStatement();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					rs = stmt.executeQuery("select * from Book where Title like '%"+ bookname.getText()+"%';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
				
						while(rs.next()){
					x=x+1;
						String title1 = rs.getString(2);
						titlelist.add(title1);
						}
				if (x==0){
						JOptionPane.showMessageDialog(null, "No books with this search criteria");

					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int a = 0; a < titlelist.size(); a++) {

					titles.addItem(titlelist.get(a));

				}
				authors.setEnabled(false);
				authorbooks.setEnabled(false);
				displaybooks.setEnabled(false);
				titles.setEnabled(true);
				findtitle.setEnabled(true);
				findauthor.setEnabled(false);
				Checkout.setEnabled(true);
				count =2;
		
				
			}
		});
		findauthor.addActionListener(new java.awt.event.ActionListener() {

			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				ResultSet rs = null ;
				Statement stmt = null ;
				int x=0;
				ArrayList<String> titlelist = new ArrayList<String>();
				authorbooks.removeAllItems();
				try {
					stmt = con.createStatement();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String fullname= (String) authors.getSelectedItem();
				String[] names= fullname.split(" ");
				
				try {
					rs=stmt.executeQuery("select b.ISBN, b.Title from Book b join Written_by w on b.ISBN=w.ISBN join Author a on a.AuthorID=w.AuthorID where a.First_Name ='"+names[0]+"'and a.Last_Name='"+names[1]+"';");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				try {
					
					while(rs.next()){
						x=x+1;
						String title1 = rs.getString(2);
						titlelist.add(title1);
					}
					if (x==0){
						JOptionPane.showMessageDialog(null, "No books with this search criteria");

					}
				
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int a = 0; a < titlelist.size(); a++) {

					authorbooks.addItem(titlelist.get(a));

				}
				authors.setEnabled(true);
				authorbooks.setEnabled(true);
				displaybooks.setEnabled(false);
				titles.setEnabled(false);
				findtitle.setEnabled(false);
				findauthor.setEnabled(true);
				Checkout.setEnabled(true);
				count=3;
			}
		});
		
	}

	public static boolean aValidDate(String expdate) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date testDate = null;
		try

		{

			testDate = sdf.parse(expdate);

		}

		catch (ParseException e)

		{

			// JOptionPane.showMessageDialog(null, "the date you provided is in
			// an invalid date" + " format.");

			return false;

		}

		if (!sdf.format(testDate).equals(expdate))

		{

			JOptionPane.showMessageDialog(null, "The date that you provided is invalid.");

			return false;

		}

		return true;

		// end isValidDate

	}

}
