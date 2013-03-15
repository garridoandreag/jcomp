
import java.io.*;
import java.util.*;

/*------------------------------------------------------------------------------------
 				MAIN
-------------------------------------------------------------------------------------*/
public class JComp{

	public static void main(String [] args){
			iniciar(args[0]);
	}

	static int iniciar(String file){

		String codigo;

		Lexico a_lex = new Lexico();
		Sintactico a_sin = new Sintactico();
		Semantico a_sem = new Semantico();
		Debugger debug = new Debugger();

		a_lex.setDebugger(debug);
		a_lex.setCodigo(file);
		if(a_lex.iniciar() != 0) {  debug.closeFile(); return 1; }

		codigo = a_lex.getCodigo();

		a_sin.setDebugger(debug);
		a_sin.setCodigo(codigo);
		if(a_sin.iniciar() != 0)  { debug.closeFile(); return 1; }
		codigo = a_sin.getCodigo();




		a_sem.setDebugger(debug);
		a_sem.setCodigo(codigo);
		if(a_sem.iniciar() != 0) {debug.closeFile(); return 1; }
		codigo = a_sem.getCodigo();


		//aki se termina el analisis del codigo....
		//ahora a ensamblar

		debug.imprimirLinea("");
		debug.imprimirLinea("");
		debug.imprimirLinea("----------------------");
		debug.imprimirLinea("CODIGO OBJETO !!");
		debug.imprimirLinea("----------------------");

		debug.imprimirLinea(codigo);


		Ensambler en = new Ensambler();
		en.setCodigo(codigo);
		en.setDebugger(debug);
		if(en.iniciar() != 0) {debug.closeFile(); return 1; }
		codigo = en.getCodigo();


		debug.imprimirLinea("");
		debug.imprimirLinea("");
		debug.imprimirLinea("----------------------");
		debug.imprimirLinea("CODIGO PARA ENSAMBLAR");
		debug.imprimirLinea("----------------------");

		debug.imprimirLinea(codigo);


		debug.imprimirLinea("");
		debug.imprimirLinea("");
		debug.imprimirLinea("----------------------");
		debug.imprimirLinea("	ENSAMBLADO");
		debug.imprimirLinea("----------------------");
		//listo ahora a ensamblar

		debug.imprimir("CREANDO .ASM ...");

		try{
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("p.asm")));
			pw.print(codigo);
			pw.close();

		}catch(IOException ioe){
			System.out.println("error creando archivo asm");

		}

		debug.imprimirLinea("OK");
		debug.imprimirLinea("");
		debug.imprimirLinea("ENSAMBLANDO ...");


        try{

			String cmd = "cmd.exe /C tasm /z /ml p.asm";

			Process proc = Runtime.getRuntime().exec(cmd);

  	        try
	        {
	            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	            	{
					debug.imprimirLinea(line);

						if(line.indexOf("Error messages:") != -1)
							{
								System.out.println(line);
							}

					}
	        } catch (IOException ioe)     { ioe.printStackTrace(); }

        	int exitVal = proc.waitFor();

        } catch (Throwable t){ t.printStackTrace(); }


		debug.imprimirLinea("");
		debug.imprimirLinea("LINKEANDO ...");
        try{

			String cmd = "cmd.exe /C tlink p.obj";

			Process proc = Runtime.getRuntime().exec(cmd);

  	        try
	        {
	            InputStreamReader isr = new InputStreamReader(proc.getInputStream());
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	            	{
					debug.imprimirLinea(line);
					}
	        } catch (IOException ioe)     { ioe.printStackTrace(); }

        	int exitVal = proc.waitFor();

        } catch (Throwable t){ t.printStackTrace(); }


		debug.imprimirLinea("");
		debug.imprimirLinea("---------------------------COMPILACION COMPLETA !!-------------");

		debug.closeFile();


		//aweboooo PUTA MADREEEE
		//despues de muchas semanas...
		//la primera ejecucion de un exe el lunes 15 de diciembre del 2008 a las 12:19am


		System.out.println("Compilacion completa.");
	return 0;
	}
}//main


