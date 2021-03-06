package jcomp;

public class Opciones
{
	private String [] _rawArgs;
	private String _PathACodigoFuente;
	private boolean isValid;
	private final char PATH_A_CODIGO_FUENTE = 'f';
	private final char ENSAMBLADOR = 'a';
	private String _ConfiguracionAsm = "nasm";

	public Opciones(String [] args)
	{
		_rawArgs = args;
		isValid = true;
	}

	public String getCodigoFuentePath()
	{
		return _PathACodigoFuente;
	}

	public String getNasmOrMasm()
	{
		return _ConfiguracionAsm;
	}

	private void parse()
	{
		int buffIndex = 0;

		do{
			if (_rawArgs[buffIndex].toCharArray()[0] != '-')
			{
				// Este debe ser el codigo fuente.
				_PathACodigoFuente = _rawArgs[buffIndex];
			}
			else
			{
				char flag = _rawArgs[buffIndex].toCharArray()[1];
				switch (flag)
				{
					case PATH_A_CODIGO_FUENTE:
						if (_PathACodigoFuente != null)
						{
							isValid = false;
							break;
						}
						buffIndex++;
						_PathACodigoFuente = _rawArgs[buffIndex];
					break;

					case ENSAMBLADOR:
						if (_ConfiguracionAsm != null)
						{
							isValid = false;
							break;
						}
						buffIndex++;
						_ConfiguracionAsm = _rawArgs[buffIndex];
					break;

				}
			}
			buffIndex++;
		}while(buffIndex < _rawArgs.length);
	}

	public boolean isValid()
	{
		parse();
		return isValid;
	}
}
