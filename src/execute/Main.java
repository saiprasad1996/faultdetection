package execute;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;


public class Main {

	static SerialPort serialPort;
	static DBConnect db = new DBConnect();
	static String ports_="";
	static String outputtext="\n";
	static JFrame frame;
	static JTextArea output;
	
	public static void main(String[] args) {
		String port=null;

		String ports[]=SerialPortList.getPortNames();
		try {
			System.out.println("\nList of available ports are : ");

			for(String p:ports){
				System.out.println(p);
				ports_=ports_+"\n"+p;
			}
			//GUI INTERFACE
			
			//port=JOptionPane.showInputDialog("Enter the port from the following list : "+ports_);
			port=JOptionPane.showInputDialog(null, "Enter the COM Port Number from the list : "+ports_, "COM Port Input", JOptionPane.QUESTION_MESSAGE);
			if(port==null)
			{
				throw new NullPointerException();
			}
			int optionchosen=JOptionPane.showConfirmDialog(null, "Click on Yes if you want to get data from the web else click on No.", "Connection Mode?", JOptionPane.YES_NO_OPTION);
			if(optionchosen==JOptionPane.YES_OPTION)
			{
				db.connectToWeb();
			}
			else if(optionchosen==JOptionPane.NO_OPTION)
			{
				db.connect();
			}
			initGUI();
			
			//End GUI SECTION
			System.out.println("\nSet port : "+port);
			//BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			//String port=br.readLine();
			System.out.println("\nPort : "+port+" is selected.\n\nData Flow Starts from here..\n____________________________");
			serialPort = new SerialPort(port);
			
			serialPort.openPort();// Open port
			serialPort.setParams(9600, 8, 1, 0);// Set params
			int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS
					+ SerialPort.MASK_DSR;// Prepare mask
			serialPort.setEventsMask(mask);// Set mask
			serialPort.addEventListener(new SerialPortReader());// Add
																// SerialPortEventListener
		} catch (SerialPortException ex) {
			System.out.println(ex);
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(NullPointerException n)
		{
			JOptionPane.showMessageDialog(null, "Error!! You need to enter the port..", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	static void initGUI()
	{
		frame=new JFrame();
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		
		
		frame.setLocationRelativeTo(null);
		frame.setSize(500, 500);
		frame.setTitle("Optical Fault Detection");
			output=new JTextArea();
			output.setEditable(false);
			output.setText("Intialising Completed..");
			output.append("\nSoftware Developed by SAI ");
			output.append("\n\nOptical Fault Detection by SAI AND JAGANNATH\nUnder the guidance of Prof. Kaliprasanna Swain\n");
			output.append("Fault Detection Software v2.0\n");
			
			JScrollPane scroll = new JScrollPane(output,
	                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scroll.setAutoscrolls(true);
			
			frame.add(scroll);
			
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	static void updateGUI(String outputstr)
	{
		
		output.append(outputstr);
	}
	
	/*
	 * In this class must implement the method serialEvent, through it we learn
	 * about events that happened to our port. But we will not report on all
	 * events but only those that we put in the mask. In this case the arrival
	 * of the data and change the status lines CTS and DSR
	 */
	
	static class SerialPortReader implements SerialPortEventListener {

		public void serialEvent(SerialPortEvent event) {
			if (event.isRXCHAR()) {// If data is available
				if (event.getEventValue() == 8) {// Check bytes count in the
													// input buffer
					// Read data, if 10 bytes available
					try {
						byte buffer[] = serialPort.readBytes(8);
						String readed = new String(buffer);
						System.out.println(readed);
						char flag = 0;
						String value[] = new String[2];

						if (flag == 'X' || flag == 'x') {
							return;
						} else {
							System.out.println(readed);
							value[0] = readed.substring(0, 4);
							value[1] = readed.substring(5, 8);
							if (value[1].equalsIgnoreCase("yes")) {
								System.out.println("db");
								//db.connect();
								db.insertIntoDb(value[0], value[1]);
							} else {
							}
							System.out.println("««Data Read from COM" + ": "
									+ readed);
							updateGUI("\n\n<<Data Read from COM Port : "+readed);
							
						}
					} catch (SerialPortException ex) {
						System.out.println(ex);
					} catch (Exception sq) {
						sq.printStackTrace();
					}
				}
			} else if (event.isCTS()) {// If CTS line has changed state
				if (event.getEventValue() == 1) {// If line is ON
					System.out.println("CTS - ON");
				} else {
					System.out.println("CTS - OFF");
				}
			} else if (event.isDSR()) {// /If DSR line has changed state
				if (event.getEventValue() == 1) {// If line is ON
					System.out.println("DSR - ON");
				} else {
					System.out.println("DSR - OFF");
				}
			}
		}
	}
}