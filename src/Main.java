import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import util.EllipticCurve;
import util.NumberFormatter;
import util.Prime;

/**
 * main program: creating GUI and integrate all classes
 */

/**
 * @author nashir
 *
 */
public class Main {

	private static Main instance;
	private static Toolkit toolkit;
	private static Dimension screenSize;
	private static Insets inset;

	private static JFrame mainFrame;
	private static JPanel ecPanel;
	private static JPanel keyPanel;
	private static JPanel filePanel;
	private static JPanel infoPanel;
	private static JPanel textPanel;
	
	private static JLabel aLabel;
	private static JTextField aField;
	private static JLabel bLabel;
	private static JTextField bField;
	private static JLabel pLabel;
	private static JTextField pField;
	private static JLabel basePointLabel;
	private static JLabel xBasePointLabel;
	private static JTextField xBasePointField;
	private static JLabel yBasePointLabel;
	private static JTextField yBasePointField;
	private static JButton ecButton;
	
	private static JButton keyGenButton;
	private static JLabel privateKeyLabel;
	private static JTextField privateKeyField;
	private static JButton privateKeyButton;
	private static JFileChooser privateKeyChooser;
	private static JLabel publicKeyLabel;
	private static JTextField publicKeyField;
	private static JButton publicKeyButton;
	private static JFileChooser publicKeyChooser;
	
	private static JLabel privateLabel;
	private static JTextField privateField;
	private static JButton privateButton;
	private static JLabel publicLabel;
	private static JTextField publicField;
	private static JButton publicButton;
	private static JLabel fileLabel;
	private static JTextField fileField;
	private static JButton fileButton;
	private static JFileChooser fileChooser;
	private static JButton encryptButton;
	private static JButton decryptButton;
	
	private static JLabel timeLabel;
	private static JTextField timeField;
	private static JLabel sizeLabel;
	private static JTextField sizeField;
	
	private static JLabel inputLabel;
	private static JTextArea inputArea;
	private static JScrollPane inputScroll;
	private static JLabel outputLabel;
	private static JTextArea outputArea;
	private static JScrollPane outputScroll;
	private static JButton outputButton;
	
	private static EllipticCurve curve;
	private static byte[] content;
	private static long[] message;
	private static long[][] tempPoint;
	
	private static HashMap<Long, Long> converterTable;
	
	/**
	 * create singleton of class Main
	 * @return
	 */
	public static Main getInstance() {
		instance = new Main();
		converterTable = new HashMap<Long, Long>();
		prepareGUI();
		return instance;
	}
	
	/**
	 * prepare GUI for the first time
	 */
	private static void prepareGUI() {
		// main frame and screen's information
		toolkit = Toolkit.getDefaultToolkit();
		mainFrame = new JFrame("myECCEG");
		screenSize = toolkit.getScreenSize();
		inset = toolkit.getScreenInsets(mainFrame.getGraphicsConfiguration());
		mainFrame.setSize(screenSize.width - inset.left - inset.right, screenSize.height - inset.top - inset.bottom);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// main layout
		GroupLayout mainLayout = new GroupLayout(mainFrame.getContentPane());
		mainLayout.setAutoCreateGaps(true);
		mainLayout.setAutoCreateContainerGaps(true);
		mainFrame.getContentPane().setLayout(mainLayout);
		
		// elliptic curve panel
		ecPanel = new JPanel();
		ecPanel.setBorder(BorderFactory.createTitledBorder("Elliptic Curve"));
		
		// ecPanel: layout
		GroupLayout ecLayout = new GroupLayout(ecPanel);
		ecLayout.setAutoCreateGaps(true);
		ecLayout.setAutoCreateContainerGaps(true);
		ecPanel.setLayout(ecLayout);
		
		// ecPanel: parameter a
		aLabel = new JLabel("a");
		aField = new JTextField();
		
		// ecPanel: parameter b
		bLabel = new JLabel("b");
		bField = new JTextField();
		
		// ecPanel: parameter p
		pLabel = new JLabel("p");
		pField = new JTextField();
		
		// ecPanel: base point
		basePointLabel = new JLabel("Base point");
		xBasePointLabel = new JLabel("x");
		xBasePointField = new JTextField();
		yBasePointLabel = new JLabel("y");
		yBasePointField = new JTextField();
		
		// ecPanel: elliptic curve set button
		ecButton = new JButton("Set Elliptic Curve");
		ecButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// set up elliptic curve
				long a = EllipticCurve.DEFAULT_A, b = EllipticCurve.DEFAULT_B ,p = EllipticCurve.DEFAULT_P;
				if (pField.getText().isEmpty()) {
					pField.setText(Long.toString(p));
				} else {
					long t = Long.parseLong(pField.getText());
					if (Prime.isPrime(t)) {
						p = t;
					} else {
						pField.setText(Long.toString(p));
						JOptionPane.showMessageDialog(null, "Parameter p must be a prime number.\nAutomatically change to default value.");
					}
				}
				
				if (aField.getText().isEmpty()) {
					aField.setText(Long.toString(a));
				} else {
					a = Long.parseLong(aField.getText());
				}
				
				if (bField.getText().isEmpty()) {
					bField.setText(Long.toString(b));
				} else {
					b = Long.parseLong(bField.getText());
				}
				
				if (!EllipticCurve.isValidParameter(a, b, p)) {
					a = EllipticCurve.DEFAULT_A;
					b = EllipticCurve.DEFAULT_B;
					JOptionPane.showMessageDialog(null, "4a^3 + 27b^2 must not be equal to 0.\nAutomatically change to default values.");
				}
				
				if (curve == null) {
					curve = new EllipticCurve(a, b, p);
				} else {
					curve.setA(a);
					curve.setB(b);
					curve.setP(p);
				}
				
				// set up base point
				if (xBasePointField.getText().isEmpty()) {
					curve.generateBasePoint();
					long[] base = curve.getBasePoint();
					xBasePointField.setText(Long.toString(base[0]));
					yBasePointField.setText(Long.toString(base[1]));
				} else if (yBasePointField.getText().isEmpty()) {
					long x = Long.parseLong(xBasePointField.getText());
					long y = curve.getY(x);
					curve.setBasePoint(x, y);
					yBasePointField.setText(Long.toString(y));
				} else {
					long x = Long.parseLong(xBasePointField.getText());
					long y = Long.parseLong(yBasePointField.getText());
					if (curve.isValidPoint(x, y)) {
						curve.setBasePoint(x, y);
					} else {
						curve.generateBasePoint();
						long[] base = curve.getBasePoint();
						xBasePointField.setText(Long.toString(base[0]));
						yBasePointField.setText(Long.toString(base[1]));
						JOptionPane.showMessageDialog(null, "Point does not satisfy curve's equation.\nAutomatically generate a new point.");
					}
				}
			}
		});
		
		// ecPanel: panel's layout
		ecLayout.setHorizontalGroup(ecLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(ecLayout.createSequentialGroup()
				.addComponent(aLabel)
				.addComponent(aField))
			.addGroup(ecLayout.createSequentialGroup()
				.addComponent(bLabel)
				.addComponent(bField))
			.addGroup(ecLayout.createSequentialGroup()
				.addComponent(pLabel)
				.addComponent(pField))
			.addComponent(basePointLabel)
			.addGroup(ecLayout.createSequentialGroup()
				.addComponent(xBasePointLabel)
				.addComponent(xBasePointField))
			.addGroup(ecLayout.createSequentialGroup()
				.addComponent(yBasePointLabel)
				.addComponent(yBasePointField))
			.addComponent(ecButton, screenSize.width / 8, screenSize.width / 4, screenSize.width / 4));
		ecLayout.setVerticalGroup(ecLayout.createSequentialGroup()
			.addGroup(ecLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(aLabel)
				.addComponent(aField))
			.addGroup(ecLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(bLabel)
				.addComponent(bField))
			.addGroup(ecLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(pLabel)
				.addComponent(pField))
			.addComponent(basePointLabel)
			.addGroup(ecLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(xBasePointLabel)
				.addComponent(xBasePointField))
			.addGroup(ecLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(yBasePointLabel)
				.addComponent(yBasePointField))
			.addComponent(ecButton));
		
		// key generation panel
		keyPanel = new JPanel();
		keyPanel.setBorder(BorderFactory.createTitledBorder("Key Generator"));
		
		// keyPanel: layout
		GroupLayout keyLayout = new GroupLayout(keyPanel);
		keyLayout.setAutoCreateGaps(true);
		keyLayout.setAutoCreateContainerGaps(true);
		keyPanel.setLayout(keyLayout);
		
		// keyPanel: key generator button
		keyGenButton = new JButton("Generate Keys");
		keyGenButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (curve == null) {
					JOptionPane.showMessageDialog(null, "No elliptic curve defined.");
					return;
				}
				
				long privateKey;
				if (privateKeyField.getText().isEmpty()) {
					privateKey = new Random().nextInt((int) curve.getP());
					while (privateKey < 0 || privateKey > curve.getP()) {
						privateKey += curve.getP();
					}
					
					if (privateKey == 0) privateKey = curve.getP()/2;
					
					privateKeyField.setText(Long.toString(privateKey));
				} else {
					privateKey = Long.parseLong(privateKeyField.getText());
				}
				
				long[] publicKey = curve.getPublicKey(privateKey);
				publicKeyField.setText("(" + Long.toString(publicKey[0]) + "," + Long.toString(publicKey[1]) + ")");
				
				privateKeyButton.setEnabled(true);
				publicKeyButton.setEnabled(true);
			}
			
		});
		
		// keyPanel: private key
		privateKeyLabel = new JLabel("Private key");
		privateKeyField = new JTextField();
		privateKeyButton = new JButton("Save As");
		privateKeyButton.setEnabled(false);
		privateKeyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (privateKeyChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = privateKeyChooser.getSelectedFile().getAbsolutePath();
					if (!path.endsWith(".pri")) {
						path += ".pri";
					}
					
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(path);
						pw.write(privateKeyField.getText());
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} finally {
						if (pw != null) pw.close();
					}
				}
			}
			
		});
		privateKeyChooser = new JFileChooser("Save private key to a file");
		privateKeyChooser.setCurrentDirectory(new File("res/private"));
		
		// keyPanel: public key
		publicKeyLabel = new JLabel("Public key");
		publicKeyField = new JTextField();
		publicKeyField.setEditable(false);
		publicKeyButton = new JButton("Save As");
		publicKeyButton.setEnabled(false);
		publicKeyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (publicKeyChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = publicKeyChooser.getSelectedFile().getAbsolutePath();
					if (!path.endsWith(".pub")) {
						path += ".pub";
					}
					
					PrintWriter pw = null;
					try {
						pw = new PrintWriter(path);
						pw.write(publicKeyField.getText());
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} finally {
						if (pw != null) pw.close();
					}
				}
			}
			
		});
		publicKeyChooser = new JFileChooser("Save public key to a file");
		publicKeyChooser.setCurrentDirectory(new File("res/public"));
		
		// keyPanel: panel's layout
		keyLayout.setHorizontalGroup(keyLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(keyGenButton, screenSize.width / 8, screenSize.width / 4, screenSize.width / 4)
			.addComponent(privateKeyLabel)
			.addGroup(keyLayout.createSequentialGroup()
				.addComponent(privateKeyField)
				.addComponent(privateKeyButton))
			.addComponent(publicKeyLabel)
			.addGroup(keyLayout.createSequentialGroup()
					.addComponent(publicKeyField)
					.addComponent(publicKeyButton)));
		keyLayout.setVerticalGroup(keyLayout.createSequentialGroup()
			.addComponent(keyGenButton)
			.addComponent(privateKeyLabel)
			.addGroup(keyLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(privateKeyField)
				.addComponent(privateKeyButton))
			.addComponent(publicKeyLabel)
			.addGroup(keyLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(publicKeyField)
				.addComponent(publicKeyButton)));
		
		// file input panel
		filePanel = new JPanel();
		filePanel.setBorder(BorderFactory.createTitledBorder("Input"));
		
		// filePanel: layout
		GroupLayout fileLayout = new GroupLayout(filePanel);
		fileLayout.setAutoCreateGaps(true);
		fileLayout.setAutoCreateContainerGaps(true);
		filePanel.setLayout(fileLayout);
		
		// filePanel: private key input
		privateLabel = new JLabel("Private key");
		privateField = new JTextField();
		privateButton = new JButton("Browse");
		privateButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (privateKeyChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = privateKeyChooser.getSelectedFile().getAbsolutePath();
					byte[] content = null;
					try {
						content = Files.readAllBytes(Paths.get(path));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					privateField.setText(new String(content));
				}
			}
		});
		
		// filePanel: public key input
		publicLabel = new JLabel("Public key");
		publicField = new JTextField();
		publicButton = new JButton("Browse");
		publicButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (publicKeyChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = publicKeyChooser.getSelectedFile().getAbsolutePath();
					byte[] content = null;
					try {
						content = Files.readAllBytes(Paths.get(path));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					publicField.setText(new String(content));
				}
			}
		});
		
		// filePanel: file input
		fileLabel = new JLabel("File");
		fileField = new JTextField();
		fileButton = new JButton("Browse");
		fileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getAbsolutePath();
					content = null;
					try {
						content = Files.readAllBytes(Paths.get(path));
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					message = null;
					message = new long[content.length];
					for (int i = 0; i < content.length; i++) {
						message[i] = content[i];
					}

					fileField.setText(path);
					inputArea.setText(new String(content));
				}
			}
		});
		fileChooser = new JFileChooser(new File("res/file"));
		
		// filePanel: encrypt button
		encryptButton = new JButton("Encrypt");
		encryptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				long startTime = System.currentTimeMillis();
				
				// calculate key for encryption
				String[] s = publicField.getText().split("[,()]");
				long[] P = new long[2];
				P[0] = Long.parseLong(s[1]);
				P[1] = Long.parseLong(s[2]);
				long[] key = curve.multiplyPoint(Long.parseLong(privateField.getText()), P);
				
				// encode message to point
				tempPoint = null;
				tempPoint = new long[message.length][2];
				for (int i = 0; i < message.length; i++) {
					tempPoint[i][0] = encryptChar(message[i]);//TODO message[i];
					Long y = converterTable.get(tempPoint[i][0]);
					if (y == null) {
						tempPoint[i][1] = curve.getY(tempPoint[i][0]);
						converterTable.put(tempPoint[i][0], tempPoint[i][1]);
					} else {
						tempPoint[i][1] = y;
					}
				}

				// encrypt each point
				String output = "";
				for (int i = 0; i < tempPoint.length; i++) {
					tempPoint[i] = curve.addPoint(tempPoint[i], key);
					output += Long.toHexString(tempPoint[i][0]) + ' ' + Long.toHexString(tempPoint[i][1]) + ' ';
				}
				
				long endTime   = System.currentTimeMillis();
				
				timeField.setText(((endTime - startTime) / 1000d) + " seconds.");
				sizeField.setText((output.length() / 1024d) + " KBs.");
				outputArea.setText(output);
				
				content = null;
			}
		});
		
		// filePanel: decrypt button
		decryptButton = new JButton("Decrypt");
		decryptButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				long startTime = System.currentTimeMillis();
				
				// calculate key for decryption
				String[] s = publicField.getText().split("[,()]");
				long[] P = new long[2];
				P[0] = Long.parseLong(s[1]);
				P[1] = Long.parseLong(s[2]);
				long[] key = curve.multiplyPoint(Long.parseLong(privateField.getText()), P);
				key[1] *= -1;
				
				// extract point from ciphertext
				tempPoint = extractPoint(message);

				// decrypt each point and decode message
				content = null;
				content = new byte[tempPoint.length];
				for (int i = 0; i < tempPoint.length; i++) {
					tempPoint[i] = curve.addPoint(tempPoint[i], key);
					if (tempPoint[i][0] >= 256) {
						tempPoint[i][0] -= curve.getP();
					}
					content[i] = (byte) decryptChar(tempPoint[i][0]);//TODO tempPoint[i][0];
				}
				
				long endTime   = System.currentTimeMillis();
				
				timeField.setText(((endTime - startTime) / 1000d) + " seconds.");
				sizeField.setText((content.length / 1024d) + " KBs.");
				outputArea.setText(new String(content));
			}
		});
		
		// filePanel: panel's layout
		fileLayout.setHorizontalGroup(fileLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(privateLabel)
			.addGroup(fileLayout.createSequentialGroup()
				.addComponent(privateField)
				.addComponent(privateButton))
			.addComponent(publicLabel)
			.addGroup(fileLayout.createSequentialGroup()
				.addComponent(publicField)
				.addComponent(publicButton))
			.addComponent(fileLabel)
			.addGroup(fileLayout.createSequentialGroup()
				.addComponent(fileField)
				.addComponent(fileButton))
			.addGroup(fileLayout.createSequentialGroup()
				.addComponent(encryptButton, screenSize.width / 16, screenSize.width / 8, screenSize.width / 4)
				.addComponent(decryptButton, screenSize.width / 16, screenSize.width / 8, screenSize.width / 4)));
		fileLayout.setVerticalGroup(fileLayout.createSequentialGroup()
			.addComponent(privateLabel)
			.addGroup(fileLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(privateField)
				.addComponent(privateButton))
			.addComponent(publicLabel)
			.addGroup(fileLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(publicField)
				.addComponent(publicButton))
			.addComponent(fileLabel)
			.addGroup(fileLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(fileField)
				.addComponent(fileButton))
			.addGroup(fileLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(encryptButton)
				.addComponent(decryptButton)));
		
		// program's runtime information panel
		infoPanel = new JPanel();
		infoPanel.setBorder(BorderFactory.createTitledBorder("Information Log"));
		
		// infoPanel: layout
		GroupLayout infoLayout = new GroupLayout(infoPanel);
		infoLayout.setAutoCreateGaps(true);
		infoLayout.setAutoCreateContainerGaps(true);
		infoPanel.setLayout(infoLayout);
		
		// infoPanel: time consumed
		timeLabel = new JLabel("Time elapsed");
		timeField = new JTextField();
		timeField.setEditable(false);
		
		// infoPanel: output's size
		sizeLabel = new JLabel("Output's size");
		sizeField = new JTextField();
		sizeField.setEditable(false);
		
		// infoPanel: panel's layout
		infoLayout.setHorizontalGroup(infoLayout.createSequentialGroup()
			.addComponent(timeLabel)
			.addComponent(timeField)
			.addComponent(sizeLabel)
			.addComponent(sizeField));
		infoLayout.setVerticalGroup(infoLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addComponent(timeLabel)
			.addComponent(timeField)
			.addComponent(sizeLabel)
			.addComponent(sizeField));
		
		// text display panel
		textPanel = new JPanel();
		textPanel.setBorder(BorderFactory.createTitledBorder("Text Display"));
		
		// textPanel: layout
		GroupLayout textLayout = new GroupLayout(textPanel);
		textLayout.setAutoCreateGaps(true);
		textLayout.setAutoCreateContainerGaps(true);
		textPanel.setLayout(textLayout);
		
		// textPanel: input text
		inputLabel = new JLabel("Input text");
		inputArea = new JTextArea();
		inputArea.setLineWrap(true);
		inputScroll = new JScrollPane(inputArea);
		
		// textPanel: output text
		outputLabel = new JLabel("Output text");
		outputArea = new JTextArea();
		outputArea.setLineWrap(true);
		outputScroll = new JScrollPane(outputArea);
		outputButton = new JButton("Save Output As");
		outputButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					String path = fileChooser.getSelectedFile().getAbsolutePath();
					
					if (content == null) {
						PrintWriter pw = null;
						try {
							pw = new PrintWriter(path);
							pw.write(outputArea.getText());
						} catch (FileNotFoundException e1) {
							e1.printStackTrace();
						} finally {
							if (pw != null) pw.close();
						}
					} else {
						FileOutputStream f;
						try {
							f = new FileOutputStream(path);
							f.write(content);
							f.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}				
			}
		});
		
		// textPanel: panel's layout
		textLayout.setHorizontalGroup(textLayout.createSequentialGroup()
			.addGroup(textLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(inputLabel)
				.addComponent(inputScroll, 3 * screenSize.width / 16, 3 * screenSize.width / 8, 3 * screenSize.width / 8))
			.addGroup(textLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(textLayout.createSequentialGroup()
					.addComponent(outputLabel, 3 * screenSize.width / 16, 3 * screenSize.width / 16, 3 * screenSize.width / 8)
					.addComponent(outputButton))
				.addComponent(outputScroll, 3 * screenSize.width / 16, 3 * screenSize.width / 8, 3 * screenSize.width / 8)));
		textLayout.setVerticalGroup(textLayout.createSequentialGroup()
			.addGroup(textLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(inputLabel)
				.addComponent(outputLabel)
				.addComponent(outputButton))
			.addGroup(textLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(inputScroll)
				.addComponent(outputScroll)));
		
		// main frame's layout
		mainLayout.setHorizontalGroup(mainLayout.createSequentialGroup()
			.addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(ecPanel, screenSize.width / 4, screenSize.width / 4, screenSize.width / 4)
				.addComponent(keyPanel, screenSize.width / 4, screenSize.width / 4, screenSize.width / 4)
				.addComponent(filePanel, screenSize.width / 4, screenSize.width / 4, screenSize.width / 4))
			.addGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(textPanel)
				.addComponent(infoPanel)));
		mainLayout.setVerticalGroup(mainLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			.addGroup(mainLayout.createSequentialGroup()
				.addComponent(ecPanel)
				.addComponent(keyPanel)
				.addComponent(filePanel))
			.addGroup(mainLayout.createSequentialGroup()
				.addComponent(infoPanel)
				.addComponent(textPanel)));
		
		// post-process
		mainFrame.add(keyPanel);
		mainFrame.add(filePanel);
		mainFrame.add(infoPanel);
		mainFrame.add(textPanel);
		mainFrame.setVisible(true);
	}
	
	private static long[][] extractPoint(long[] longs) {
		long[] retval = new long[longs.length];
		String s = "";
		
		int it = 0;
		for (int i = 0; i < longs.length; i++) {
			if (' ' == (char) longs[i]) {
				retval[it] = NumberFormatter.parseHexToLong(s);
				it++;
				s = "";
			} else {
				s += (char) longs[i];
			}
		}
		
		long[][] retval2 = new long[it/2][2];
		for (int i = 0; i < it/2; i++) {
			retval2[i][0] = retval[2*i];
			retval2[i][1] = retval[2*i+1];
		}
		return retval2;
	}
	
	private static long encryptChar(long i){
		//enkripsi dari nilai plain karakter menjadi point enkripsi
		while (i < 0) {
			i += curve.getP();
		}
		long x = ((i * EllipticCurve.AUX_BASE_K) % curve.getP()) + 1;
		x %= curve.getP();
		while (!curve.isValid(x)){
			x = x + 1;
			x %= curve.getP();
		}
		return x;
	}
	
	private static long decryptChar(long x){
		//dekripsi point karakter menjadi plain
		return (long)(Math.round((x-1)/EllipticCurve.AUX_BASE_K));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Main.getInstance();
	}

}
