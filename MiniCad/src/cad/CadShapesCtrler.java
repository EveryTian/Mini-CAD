package cad;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Vector;

import cad.shape.CadCircle;
import cad.shape.CadLine;
import cad.shape.CadRect;
import cad.shape.CadShape;
import cad.shape.CadShape.ShapeType;
import cad.shape.CadText;

public class CadShapesCtrler {

	private class FileCtrler {

		private class StringToBytes {

			int bytesLength;
			int bytesLinesNum; // 0xF as the length of one line¡£
			byte[] bytes;
			byte[] paddingBytes;

			StringToBytes(String string) {
				bytes = string.getBytes(Charset.defaultCharset());
				bytesLength = bytes.length;
				bytesLinesNum = bytesLength >> 4;
				if ((bytesLength & 0xF) != 0) {
					++bytesLinesNum;
				}
				paddingBytes = new byte[(bytesLinesNum << 4) - bytesLength];
			}
		}

		String fileName;
		final byte[] fileHeader = { // File header shall be these bytes:
			'.', 'C', 'A', 'D', 0, 0x19, (byte) 0xA0, (byte) 0x89, (byte) 0xCC, 0
		};

		void save() throws IOException {
			DataOutputStream out = new DataOutputStream(new FileOutputStream(fileName));
			byte[] lineBytes = new byte[14];
			for (int i = 0; i < 10; ++i) {
				lineBytes[i] = fileHeader[i];
			}
			int cadShapesVectorSize = cadShapesVector.size();
			lineBytes[10] = (byte) ((cadShapesVectorSize >> 24) & 0xFF);
			lineBytes[11] = (byte) ((cadShapesVectorSize >> 16) & 0xFF);
			lineBytes[12] = (byte) ((cadShapesVectorSize >> 8) & 0xFF);
			lineBytes[13] = (byte) ((cadShapesVectorSize >> 0) & 0xFF);
			out.write(lineBytes);
			out.write(getCheckValueBytes(lineBytes));
			for (CadShape cadShape : cadShapesVector) {
				lineBytes[1] = (byte) cadShape.getColor().getRed();
				lineBytes[2] = (byte) cadShape.getColor().getGreen();
				lineBytes[3] = (byte) cadShape.getColor().getBlue();
				lineBytes[4] = (byte) cadShape.getColor().getAlpha();
				switch (cadShape.getShapeType()) {
				case CIRCLE: {
					lineBytes[0] = 'C';
					CadCircle cadCircle = (CadCircle) cadShape;
					short posX = (short) (cadCircle.getPosX() & 0xFFFF);
					short posY = (short) (cadCircle.getPosY() & 0xFFFF);
					short width = (short) (cadCircle.getWidth() & 0xFFFF);
					short height = (short) (cadCircle.getHeight() & 0xFFFF);
					lineBytes[5] = (byte) ((posX >> 8) & 0xFF);
					lineBytes[6] = (byte) (posX & 0xFF);
					lineBytes[7] = (byte) ((posY >> 8) & 0xFF);
					lineBytes[8] = (byte) (posY & 0xFF);
					lineBytes[9] = (byte) ((width >> 8) & 0xFF);
					lineBytes[10] = (byte) (width & 0xFF);
					lineBytes[11] = (byte) ((height >> 8) & 0xFF);
					lineBytes[12] = (byte) (height & 0xFF);
					lineBytes[13] = (byte) cadShape.getThickness();
					out.write(lineBytes);
					out.write(getCheckValueBytes(lineBytes));
					break;
				}
				case LINE: {
					lineBytes[0] = 'L';
					CadLine cadLine = (CadLine) cadShape;
					short x1 = (short) (cadLine.getX1() & 0xFFFF);
					short y1 = (short) (cadLine.getY1() & 0xFFFF);
					short x2 = (short) (cadLine.getX2() & 0xFFFF);
					short y2 = (short) (cadLine.getY2() & 0xFFFF);
					lineBytes[5] = (byte) ((x1 >> 8) & 0xFF);
					lineBytes[6] = (byte) (x1 & 0xFF);
					lineBytes[7] = (byte) ((y1 >> 8) & 0xFF);
					lineBytes[8] = (byte) (y1 & 0xFF);
					lineBytes[9] = (byte) ((x2 >> 8) & 0xFF);
					lineBytes[10] = (byte) (x2 & 0xFF);
					lineBytes[11] = (byte) ((y2 >> 8) & 0xFF);
					lineBytes[12] = (byte) (y2 & 0xFF);
					lineBytes[13] = (byte) cadShape.getThickness();
					out.write(lineBytes);
					out.write(getCheckValueBytes(lineBytes));
					break;
				}
				case RECT: {
					lineBytes[0] = 'R';
					CadRect cadRect = (CadRect) cadShape;
					short posX = (short) (cadRect.getPosX() & 0xFFFF);
					short posY = (short) (cadRect.getPosY() & 0xFFFF);
					short width = (short) (cadRect.getWidth() & 0xFFFF);
					short height = (short) (cadRect.getHeight() & 0xFFFF);
					lineBytes[5] = (byte) ((posX >> 8) & 0xFF);
					lineBytes[6] = (byte) (posX & 0xFF);
					lineBytes[7] = (byte) ((posY >> 8) & 0xFF);
					lineBytes[8] = (byte) (posY & 0xFF);
					lineBytes[9] = (byte) ((width >> 8) & 0xFF);
					lineBytes[10] = (byte) (width & 0xFF);
					lineBytes[11] = (byte) ((height >> 8) & 0xFF);
					lineBytes[12] = (byte) (height & 0xFF);
					lineBytes[13] = (byte) cadShape.getThickness();
					out.write(lineBytes);
					out.write(getCheckValueBytes(lineBytes));
					break;
				}
				case TEXT: {
					lineBytes[0] = 'T';
					CadText cadText = (CadText) cadShape;
					short posX = (short) (cadText.getPosX() & 0xFFFF);
					short posY = (short) (cadText.getPosY() & 0xFFFF);
					short size = (short) cadText.getSize();
					lineBytes[5] = (byte) ((posX >> 8) & 0xFF);
					lineBytes[6] = (byte) (posX & 0xFF);
					lineBytes[7] = (byte) ((posY >> 8) & 0xFF);
					lineBytes[8] = (byte) (posY & 0xFF);
					lineBytes[9] = (byte) ((size >> 8) & 0xFF);
					lineBytes[10] = (byte) (size & 0xFF);
					StringToBytes stringToBytes = new StringToBytes(cadText.getText());
					lineBytes[11] = (byte) ((stringToBytes.bytesLength >> 8) & 0xFF);
					lineBytes[12] = (byte) (stringToBytes.bytesLength & 0xFF);
					lineBytes[13] = (byte) (stringToBytes.bytesLinesNum & 0xFF);
					out.write(lineBytes);
					out.write(getCheckValueBytes(lineBytes));
					out.write(stringToBytes.bytes);
					out.write(stringToBytes.paddingBytes);
					break;
				}
				default:
					break;
				}
			}
			out.flush();
			out.close();
		}

		boolean read() throws IOException {
			DataInputStream in;
			try {
				in = new DataInputStream(new FileInputStream(fileName));
			} catch (FileNotFoundException e) {
				throw e;
			}
			byte[] headerLine = read16Bytes(in);
			if (headerLine == null) {
				in.close();
				return false;
			}
			for (int i = 0; i < 10; ++i) {
				if (headerLine[i] != fileHeader[i]) {
					in.close();
					return false;
				}
			}
			byte[] headerLineCheckValue = getCheckValueBytes(headerLine);
			if (headerLineCheckValue[0] != headerLine[14] || headerLineCheckValue[1] != headerLine[15]) { 
				in.close();
				return false;
			}
			int shapesNum = bytesToInt(headerLine[0x0A], headerLine[0x0B], headerLine[0x0C], headerLine[0x0D]);
			Vector<CadShape> newCadShapesVector = new Vector<CadShape>();
			for (int i = 0; i < shapesNum; ++i) {
				byte[] line = read16Bytes(in);
				if (line == null || line.length != 16) {
					in.close();
					return false;
				}
				byte[] lineCheckValue = getCheckValueBytes(line);
				if (lineCheckValue[0] != line[14] || lineCheckValue[1] != line[15]) {
					in.close();
					return false;
				}
				// Variable lineXY means the short type number stored at line[0xX] and line[0xY]:
				int line56 = bytesToShort(line[5], line[6]);
				int line78 = bytesToShort(line[7], line[8]);
				int line9A = bytesToShort(line[9], line[0xA]);
				int lineBC = bytesToShort(line[0xB], line[0xC]);
				int lineD = line[0xD]; // The byte number store at line[0xD].
				switch ((char) line[0]) {
				case 'C':
					// For circle shape:
					// line56: posX
					// line78: posY
					// line9A: width
					// lineBC: height
					// lineD: thickness
					newCadShapesVector.add(new CadCircle(line56, line78, line9A, lineBC,
							new Color(line[1] & 0xFF, line[2] & 0xFF, line[3] & 0xFF, line[4] & 0xFF), lineD & 0xFF));
					break;
				case 'L':
					// For line shape:
					// line56: x1
					// line78: y1
					// line9A: x2
					// lineBC: y2
					// lineD: thickness
					newCadShapesVector.add(new CadLine(line56, line78, line9A, lineBC,
							new Color(line[1] & 0xFF, line[2] & 0xFF, line[3] & 0xFF, line[4] & 0xFF), lineD & 0xFF));
					break;
				case 'R':
					// For rectangle shape:
					// line56: posX
					// line78: posY
					// line9A: width
					// lineBC: height
					// lineD: thickness
					newCadShapesVector.add(new CadRect(line56, line78, line9A, lineBC,
							new Color(line[1] & 0xFF, line[2] & 0xFF, line[3] & 0xFF, line[4] & 0xFF), lineD & 0xFF));
					break;
				case 'T': {
					// For text shape:
					// line56: posX
					// line78: posY
					// line9A: size
					// lineBC: the bytes length of string
					// lineD: the number of the lines the string bytes occupy
					byte[] textBytes = new byte[lineBC];
					int stringBytesOffset = 0;
					for (int stringBytesLineNum = 0; stringBytesLineNum < lineD; ++stringBytesLineNum) {
						byte[] stringBytesLine = read16Bytes(in);
						if (stringBytesLine == null || stringBytesLine.length != 16) {
							in.close();
							return false;
						}
						for (int j = 0; j < 16; ++j) {
							if (stringBytesOffset < lineBC) {
								textBytes[stringBytesOffset++] = stringBytesLine[j];
							}
						}
					}
					if (stringBytesOffset != lineBC) {
						in.close();
						return false;
					}
					String text = new String(textBytes);
					newCadShapesVector.add(new CadText(text, line56, line78, line9A,
							new Color(line[1] & 0xFF, line[2] & 0xFF, line[3] & 0xFF, line[4] & 0xFF)));
					break;
				}
				default:
					in.close();
					return false;
				}
			}
			in.close();
			cadShapesVector.clear();
			cadShapesVector = newCadShapesVector;
			return true;
		}
		
		private byte[] read16Bytes(DataInputStream in) throws IOException {
			byte[] returnBytes = new byte[16];
			try {
				for (int i = 0; i < 16; ++i) {
					returnBytes[i] = in.readByte();
				}
			} catch (IOException e) {
				return null;
			}
			return returnBytes;
		}
		
		private int bytesToInt(byte byte0, byte byte1, byte byte2, byte byte3) { 
			return (((int) byte0 & 0xFF) << 24) | (((int) byte1 & 0xFF) << 16) |
					(((int) byte2 & 0xFF) << 8) | ((int) byte3 & 0xFF);
		}
		
		private int bytesToShort(byte byte0, byte byte1) {
			return (int) (short) (((int) byte0 & 0xFF) << 8) | (((int) byte1 & 0xFF));
		}
		
		private byte[] getCheckValueBytes(byte[] lineBytes) {
			byte[] bytes = new byte[2];
			bytes[0] = (byte) (lineBytes[0] ^ lineBytes[2] ^ lineBytes[4] ^ lineBytes[6] ^ lineBytes[8] ^ lineBytes[10] ^ lineBytes[12]);
			bytes[1] = (byte) (lineBytes[1] ^ lineBytes[3] ^ lineBytes[5] ^ lineBytes[7] ^ lineBytes[9] ^ lineBytes[11] ^ lineBytes[13]);
			return bytes;
		}
		
	}

	private FileCtrler fileCtrler = new FileCtrler();
	private Vector<CadShape> cadShapesVector = new Vector<CadShape>();

	public void drawAllShapes(Graphics2D g2d) {
		for (CadShape cadShape : cadShapesVector) {
			cadShape.draw(g2d);
		}
	}

	public void add(CadShape cadShape) {
		if (cadShape != null && cadShape.getShapeType() != ShapeType.UNDEFINED) {
			cadShapesVector.add(cadShape);
		}
	}

	public void remove(CadShape cadShape) {
		if (cadShape != null && cadShape.getShapeType() != ShapeType.UNDEFINED) {
			cadShapesVector.remove(cadShape);
		}
	}

	public void clear() {
		cadShapesVector.clear();
	}

	public int getCadShapesNum() {
		return cadShapesVector.size();
	}

	public CadShape getCadShapeByCoord(int x, int y) {
		for (CadShape cadShape : cadShapesVector) {
			if (cadShape.containsCoord(x, y)) {
				return cadShape;
			}
		}
		return null;
	}

	public void setFileName(String fileName) {
		fileCtrler.fileName = fileName;
	}
	
	public boolean getIsFileNameSet() {
		return fileCtrler.fileName != null && !fileCtrler.fileName.isEmpty();
	}
	
	public void save() throws IOException {
		fileCtrler.save();
	}
	
	public boolean read() throws IOException {
		return fileCtrler.read();
	}

}
