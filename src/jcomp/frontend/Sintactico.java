/*------------------------------------------------------------------------------------
 				ANALISIS SINTACTICO
	Todo:
		Usar una gramatica como esta: https://en.wikipedia.org/wiki/Extended_Backus%E2%80%93Naur_Form
-------------------------------------------------------------------------------------*/

package jcomp.frontend;

import jcomp.automata.*;
import jcomp.util.Log;

public class Sintactico
{
	private String codigo_fuente;
	private Log debug;

	public Sintactico()
	{
		this.debug = Log.getInstance();
	}

	public void setCodigo(String cf)
	{
		codigo_fuente = cf;
	}

	public String getCodigo()
	{
		return codigo_fuente;
	}

	public int iniciar()
	{
		debug.imprimirEncabezado( "ANALISIS SINTACTICO:" );

		// primero checar que este bien, ya despues ornaizar el codigo para el semantico
		String s [] = codigo_fuente.split("\n");
		String cf = "";

		// pegar el codigo en una sola linea
		// y ya quee para el analizis sintactico
		// solo necesitamos saber que es un id, o un numero
		// y no el numero en si
		for(int a=0; a<s.length; a++)
		{
			if(s[a].startsWith("IDENTIFICADOR_")) s[a]="<id>";
			if(s[a].startsWith("VALOR_NUMERO_")) s[a]="NUM";
			if(s[a].startsWith("TIPO_")) s[a]="TIPO";
			if(s[a].startsWith("STRING_")) s[a]="STRING";
			cf += s[a]+" ";
		}

		// crear las producciones para la gramatica
		// y despues se las pasare al automata
		Produccion [] prod =
		{
		new Produccion("<PROGRAMA>","$ <instruccion_basica> FINAL"),

		new Produccion("<instruccion_basica>","<metodo_def>"),
		new Produccion("<instruccion_basica>","<def_global>"),
		new Produccion("<instruccion_basica>","<instruccion_basica> <instruccion_basica>"),

		new Produccion("<def_global>", "CONTROL_DEF <variable_declarator>"),

		new Produccion("<casi_metodo_def>", "CONTROL_DEF TIPO <casi_llamada>"),
		new Produccion("<casi_metodo_def>", "<casi_metodo_def> TIPO CORCHETE_ABRE CORCHETE_CIERRA <id>"),
		new Produccion("<casi_metodo_def>", "<casi_metodo_def> TIPO <id> PUNTUACION_COMA"),
		new Produccion("<casi_metodo_def>", "<casi_metodo_def> PUNTUACION_COMA TIPO <id>"),

		new Produccion("<metodo_def>", "<casi_metodo_def> PARENTESIS_CIERRA <statement_block>"),
		new Produccion("<metodo_def>", "<casi_metodo_def> TIPO <id> PARENTESIS_CIERRA <statement_block>"),
		new Produccion("<metodo_def>", "<casi_metodo_def> <decl_p_met> PARENTESIS_CIERRA <statement_block>"),

		new Produccion("<decl_p_met>", "TIPO <id> PUNTUACION_COMA TIPO <id>"),
		new Produccion("<decl_p_met>", "<decl_p_met> PUNTUACION_COMA TIPO <id>"),


		new Produccion("<casi_if>", "CONTROL_IF PARENTESIS_ABRE"),
		new Produccion("<error_1>", "CONTROL_IF <id>"),
		new Produccion("<error_1>", "CONTROL_IF <op_bool>"),
		new Produccion("<if>", "<casi_if> <expression_booleana> PARENTESIS_CIERRA <statement_block>"),


		new Produccion("<casi_while>", "CONTROL_WHILE PARENTESIS_ABRE"),
		new Produccion("<while>", "<casi_while> <expression_booleana> PARENTESIS_CIERRA <statement_block>"),

		new Produccion("<expression_booleana>", "<id> <op_bool> <id>"),
		new Produccion("<expression_booleana>", "<id> <op_bool> <expression>"),
		new Produccion("<expression_booleana>", "<id> <op_bool> <expression_booleana>"),
		new Produccion("<expression_booleana>", "<expression_boleana> <op_bool> <id>"),
		new Produccion("<expression_booleana>", "<expression> <op_bool> <expression_booleana>"),
		new Produccion("<expression_booleana>", "<expression_boleana> <op_bool> <expression>"),
		new Produccion("<expression_booleana>", "<expression> <op_bool> <id>"),
		new Produccion("<expression_booleana>", "<expression> <op_bool> <expression>"),
		new Produccion("<expression_booleana>", "PARENTESIS_ABRE <expression_booleana> PARENTESIS_CIERRA"),
		new Produccion("<expression_booleana>", "<expression_booleana> <op_bool> <expression_booleana>"),

		new Produccion("<expression_booleana>", "<llamada> <op_bool> <expression>"),
		new Produccion("<expression_booleana>", "<llamada> <op_bool> <llamada>"),
		new Produccion("<expression_booleana>", "<expression> <op_bool> <llamada>"),
		new Produccion("<expression_booleana>", "<expression_booleana> <op> <expression>"),
		new Produccion("<expression_booleana>", "<expression> <op> <expression_booleana>"),
		new Produccion("<expression_booleana>", "<id> <op_bool> <llamada>"),
		new Produccion("<expression_booleana>", "<llamada> <op_bool> <id>"),
		new Produccion("<expression_booleana>", "<expression_booleana> <op> <id>"),
		new Produccion("<expression_booleana>", "<id> <op> <expression_booleana>"),
		new Produccion("<id>", "PARENTESIS_ABRE <id> PARENTESIS_CIERRA"),

		new Produccion("<op_bool>", "BOL_MENOR_QUE"),
		new Produccion("<op_bool>", "BOL_MAYOR_QUE"),
		new Produccion("<op_bool>", "BOL_MENOR_QUE ASIGNA"), //aqui falta manosear
		new Produccion("<op_bool>", "BOL_MAYOR_QUE ASIGNA"),
		new Produccion("<op_bool>", "ASIGNA ASIGNA"),

		new Produccion("<asignacion>", "<id> ASIGNA <expression> PUNTUACION_PUNTO_COMA"),
		new Produccion("<asignacion>", "<id> ASIGNA <id> PUNTUACION_PUNTO_COMA"),
		new Produccion("<asignacion>", "<id> ASIGNA <llamada> PUNTUACION_PUNTO_COMA"),
		new Produccion("<asignacion>", "<id> CORCHETE_ABRE <expression> CORCHETE_CIERRA ASIGNA <expression> PUNTUACION_PUNTO_COMA"),

		new Produccion("<variable_declarator>", "TIPO <id> CORCHETE_ABRE <expression> CORCHETE_CIERRA PUNTUACION_PUNTO_COMA"),
		new Produccion("<variable_declarator>", "TIPO <id> PUNTUACION_PUNTO_COMA"),

		new Produccion("<statement_block>", "LLAVE_ABRE <statement> LLAVE_CIERRA"),
		new Produccion("<statement_block>", "LLAVE_ABRE LLAVE_CIERRA"),

		new Produccion("<statement>", "<statement> <statement>"),
		new Produccion("<statement>", "<variable_declaration>"),
		new Produccion("<statement>", "<variable_declarator>"),
		new Produccion("<statement>", "<asignacion>"),
		new Produccion("<statement>", "<if>"),
		new Produccion("<statement>", "<while>"),
		new Produccion("<statement>", "<retorno>"),

		new Produccion("<retorno>", "CONTROL_RETORNO <id> PUNTUACION_PUNTO_COMA"),
		new Produccion("<retorno>", "CONTROL_RETORNO <expression> PUNTUACION_PUNTO_COMA"),
		new Produccion("<retorno>", "CONTROL_RETORNO <llamada> PUNTUACION_PUNTO_COMA"),

		new Produccion("<expression>", "<literal_expression>"),

		new Produccion("<expression>", "<expression> <op> <expression>"),
		new Produccion("<expression>", "<id> <op> <expression>"),
		new Produccion("<expression>", "<expression> <op> <id>"),
		new Produccion("<expression>", "<id> <op> <id>"),

		new Produccion("<expression>", "<literal_expression>"),
		new Produccion("<expression>", "PARENTESIS_ABRE <expression> PARENTESIS_CIERRA"),


		new Produccion("<ARGS>", "<expression> PUNTUACION_COMA <expression>"),
		new Produccion("<ARGS>", "<expression> PUNTUACION_COMA <id>"),
		new Produccion("<ARGS>", "<id> PUNTUACION_COMA <expression>"),
		new Produccion("<ARGS>", "<id> PUNTUACION_COMA <id>"),
		new Produccion("<ARGS>", "<ARGS> PUNTUACION_COMA <expression>"),
		new Produccion("<ARGS>", "<ARGS> PUNTUACION_COMA <id>"),
		new Produccion("<ARGS>", "<ARGS> <op> <expression>"),
		new Produccion("<ARGS>", "<ARGS> <op> <id>"),

		// Esta regla tiene que incluir el `casi_llamada`, de lo contrario seria un prefijo de `variable_declarator`
		// habilita esto: #funcion( a[3] );
		new Produccion("<casi_llamada>", "<casi_llamada> <id> CORCHETE_ABRE <expression> CORCHETE_CIERRA"),
		new Produccion("<casi_llamada>", "PUNTUACION_GATO <id> PARENTESIS_ABRE"),

		new Produccion("<llamada>", "<casi_llamada> <expression> PARENTESIS_CIERRA"),
		new Produccion("<llamada>", "<casi_llamada> <id> PARENTESIS_CIERRA"),
		new Produccion("<llamada>", "<casi_llamada> PARENTESIS_CIERRA"),
		new Produccion("<llamada>", "<casi_llamada> <ARGS> PARENTESIS_CIERRA"),


		new Produccion("LLAVE_ABRE <statement>", "LLAVE_ABRE <llamada> PUNTUACION_PUNTO_COMA"),
		new Produccion("<statement>", "<statement> <llamada> PUNTUACION_PUNTO_COMA"),

		new Produccion("<op> <expression>", "<op> <llamada>"),
		new Produccion("<expression> <op>", "<llamada> <op>"),
		new Produccion("PUNTUACION_COMA <expression>", "PUNTUACION_COMA <llamada>"),
		new Produccion("<expression> PUNTUACION_COMA", "<llamada> PUNTUACION_COMA"),
		new Produccion("PARENTESIS_ABRE <expression>", "PARENTESIS_ABRE <llamada>"),
		new Produccion("<expression> PARENTESIS_CIERRA", "<llamada> PARENTESIS_CIERRA"),

		new Produccion("<op>", "OP_SUMA"),
		new Produccion("<op>", "OP_RESTA"),
		new Produccion("<op>", "OP_MULTIPLICACION"),
		new Produccion("<op>", "OP_DIVISION"),

		new Produccion("<literal_expression>", "NUM"),
		new Produccion("<literal_expression>", "STRING")

		};

		Automata aut = new Automata(prod, cf);
		String resultado = aut.iniciar();
		debug.imprimirLinea( resultado );

		if( !resultado.endsWith("<PROGRAMA> "))
		{
			System.out.println("Error de Syntaxis.");
			System.err.println("Error de Syntaxis.");
			return 1;
		}

		return 0;
	}
}

