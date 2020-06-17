package gestoreRegistrazioni;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;


class GesticiRegistrazioni {
	
	private static final String path_dest = "D:/REGISTRAZIONI POLIMI/Year 2019-2020/2nd semester/";
	
	private static final String[] prof = {"resta f.", "piegari l.", "rossi m. g.", "scattolini r."};
	private static final String[] materie = {"MSM", "ME", "SE", "AMC"};
	
	private static final String reg_serial_number = "0";
	private static String path_reg;
	@SuppressWarnings("unchecked")
	private static ArrayList<File>[] listeRegs = new ArrayList[materie.length];
	
	
	//--------------------------------------------------------------------------------------------//
	

	static boolean trovaRegistratore() { 
		String lettera_cor;
		for (int i = 68; i < 91; i++) {
			lettera_cor = Character.toString((char)i);
			if ( DiskUtils.getSerialNumber(lettera_cor).equals(reg_serial_number) ) {
				path_reg = lettera_cor+":/VOICE/";
				return true;
			}
		}
		return false;
	}
	
	static int controlli() {
		boolean tutto_vuoto = true;
		for (int i = 0; i < listeRegs.length; i++) {
			if ( !listeRegs[i].isEmpty() ) {
				tutto_vuoto = false;
				for ( File f : listeRegs[i] ) {
					if ( !f.getName().matches("\\d{6}_\\d{3}\\.MP3") ) return 3;  
					for ( File f1 : listeRegs[i] ) {
						if ( f.hashCode() != f1.hashCode()
						&& f.getName().substring(0,7).equals(f1.getName().substring(0,7)) )
							return 2;
					}
				}
			} 
		}
		if ( tutto_vuoto ) return 1;
		else return 0;
	}
	
	static void creaListeRegs() {
		File file_lista_temp;
		File[] lista_temp;
		GUI.regTot = 0;
		for (int i = 0; i < materie.length; i++) {
			file_lista_temp = new File(path_reg+materie[i]);
			lista_temp = file_lista_temp.listFiles();
			listeRegs[i] = new ArrayList<>();
			for ( File f : lista_temp ) 
				listeRegs[i].add(f);
			GUI.regTot+=listeRegs[i].size();
		}
		if ( GUI.regTot!=0 ) GUI.valForInc = (100-GUI.valProgressivo)/GUI.regTot;
	}
	
	static void copia() {
		File file_old;
		File file_new;
		for (int i = 0; i < listeRegs.length; i++) {
			for ( File f : listeRegs[i] ) {
				file_old = new File(path_reg+materie[i]+"/"+f.getName());
				String new_name = rinomina(f, i);
				file_new = new File(path_dest+materie[i]+"/"+new_name);
				try {
					Files.copy(file_old.toPath(), file_new.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
					MP3File m = new MP3File(file_new);
					m.getID3v2Tag().setSongTitle(new_name);
					m.getID3v2Tag().setAlbumTitle(materie[i]);
					m.getID3v2Tag().setLeadArtist(prof[i]);
					m.save();
				} catch ( FileAlreadyExistsException e1) {
					GUI.taskOutput.append(String.format("<!> REG. "+file_old.getPath()+"\n E' GIA' PRESENTE IN "+file_new.getPath()+"\n"));
					continue;
				} catch ( IOException e2 ) {
					GUI.taskOutput.append(String.format("<!> REG. "+file_old.getPath()+" NON E' STATO COPIATA"+"\n"));
					continue;
				} catch ( TagException te ) {
					GUI.taskOutput.append(String.format("<!> ERRORE NELL'ASSEGNAZIONE DI TAG PER REG. "+file_old.getPath()+"\n"));
					continue;
				}
				file_old.delete();
				GUI.regCopiate++;
				GUI.aggiorna("<OK> Reg. "+file_old.getPath()+" è stata copiata", GUI.valProgressivo+=GUI.valForInc);
			}
		}
	}
	
	static String rinomina ( File f, int i) {
		String n_old = f.getName();
		String anno = n_old.substring(0,2);
		String mese = n_old.substring(2,4);
		String giorno = n_old.substring(4,6);
		return prof[i]+" "+anno+"."+mese+"."+giorno+"_001.MP3";
	}
}
