/**
 * 
 */
package gestoreRegistrazioni;

import java.awt.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.text.DefaultCaret;


/**
 * @author Giuseppe Longo
 *
 */
class GUI extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String pathOneDrive = "C:/Users/peppe/AppData/Local/Microsoft/OneDrive/OneDrive.exe";
	
	private static JProgressBar progressBar;
	static JTextArea taskOutput;
	static int valProgressivo = 10;
	static int valForInc;
	static int regTot, regCopiate;
	private static JComponent newContentPane;
	

	public static void main(String[] args) throws IOException {

		createAndShowGUI();
		
		if ( !GesticiRegistrazioni.trovaRegistratore() ) {
			taskOutput.setForeground(Color.RED);
			taskOutput.append(String.format("REGISTRATORE NON TROVATO\n"));
			newContentPane.setCursor(null);
			Toolkit.getDefaultToolkit().beep();
			
			return;
		}
		
		aggiorna("REGISTRATORE TROVATO", valProgressivo);

		GesticiRegistrazioni.creaListeRegs();
				
		switch ( GesticiRegistrazioni.controlli() ) {
		case 1: taskOutput.setForeground(Color.RED);
		taskOutput.append(String.format("NON CI SONO REGISTRAZIONI DA COPIARE\n"));
		newContentPane.setCursor(null);
		Toolkit.getDefaultToolkit().beep();
		return;
		case 2: taskOutput.setForeground(Color.RED);
		taskOutput.append(String.format("CI SONO REGISTRAZIONI CON STESSA DATA: INTERVIENI\n"));
		newContentPane.setCursor(null);
		Toolkit.getDefaultToolkit().beep();
		return;
		case 3: taskOutput.setForeground(Color.RED);
		taskOutput.append(String.format("PATTERN FILE SCONOSCIUTO: INTERVIENI\n"));
		newContentPane.setCursor(null);
		Toolkit.getDefaultToolkit().beep();
		return;
		}
		
		aggiorna("CI SONO "+regTot+" REGISTRAZIONI DA COPIARE", valProgressivo+=10);
		
		GesticiRegistrazioni.copia();
		
		aggiorna("OPERAZIONE CONCLUSA: "+regCopiate+" su "+regTot, 100);
		newContentPane.setCursor(null);
		Toolkit.getDefaultToolkit().beep();
		
		//apriOneDrive();
		
	}

	public GUI() {
		
		super(new BorderLayout());
		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		taskOutput = new JTextArea(6, 40);
		DefaultCaret caret = (DefaultCaret) taskOutput.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		taskOutput.setMargin(new Insets(5,5,5,5));
		taskOutput.setEditable(false);

		JPanel panel = new JPanel();
		panel.add(progressBar);

		add(panel, BorderLayout.PAGE_START);
		add(new JScrollPane(taskOutput), BorderLayout.CENTER);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	}


	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("Gestore Registrazioni");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		//Create and set up the content pane.
		newContentPane = new GUI();
		newContentPane.setOpaque(true); //content panes must be opaque
		frame.setContentPane(newContentPane);
		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setAlwaysOnTop(true);
		frame.setVisible(true);
	}
	
	static void aggiorna ( String s, int i) {
		if (i>=0) progressBar.setValue(i);
		taskOutput.append(String.format(s+"\n"));
	}
	
	private static void apriOneDrive() {
		if ( regCopiate!=0 ) {
			try {
				aggiorna("\nSINCRONIZZAZIONE CON ONEDRIVE...", -1);
				Runtime.getRuntime().exec(pathOneDrive); 
			}
			catch(Exception e){
				Frame er = new Frame();
				er.setAlwaysOnTop(true);
				JOptionPane.showMessageDialog(er,
				    "IMPOSSIBILE ESEGUIRE ONEDRIVE\nSINCRONIZZAZIONE SOSPESA",
				    null,
				    JOptionPane.ERROR_MESSAGE);
			}
		}	
	}
	
}
