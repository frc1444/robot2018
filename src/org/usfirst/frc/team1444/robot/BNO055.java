package org.usfirst.frc.team1444.robot;

import edu.wpi.first.wpilibj.I2C;

import java.nio.*;

public class BNO055 {

	public BNO055() {
		m_i2c = new I2C(I2C.Port.kOnboard, 0x28);
		//Select page 0
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		//Set units for m/s2, Rps, Radians, degC,
		//Pitch: -Pi to Pi Clockwise+, Roll: -Pi/2 - Pi/2, Heading/Yaw: 0-2Pi clockwise+
		m_i2c.write(REG_PAGE0.UNIT_SELECT, 0x6);
		
		
	};
	
	public void Calibrate() {
		
	};
	
	public boolean isCalibrated() {
		byte[] result = {0};
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.read(REG_PAGE0.CALIB_STAT, 1, result);
		//This actually returns <SYS><GYR><ACC><MAG>
		//3=Calibrated, 0=not. We check SYS status.
		return (result[0]>0x3F);
	};
	
	public void SetMode(int mode) {
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.write(REG_PAGE0.SYS_MODE, (byte)mode);
	};
	
	public int GetRawMagHeading() {
		byte[] result = {0,0,0,0,0,0};
		int value;
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.read(REG_PAGE0.MAG_X_LB, 6, result);
		
		value = (result[1]<<8) | result[0];
		return value;
	};
	
	public int GetEulerHeading() {
		ByteBuffer result = ByteBuffer.allocateDirect(6);
		result.order(ByteOrder.LITTLE_ENDIAN);
		int value;
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.read(REG_PAGE0.EUL_HEAD_LB, 6, result);
		
		value = result.getShort(0);
		return value;
	};

	public int GetEulerRoll() {
		byte[] result = {0,0,0,0,0,0};
		int value;
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.read(REG_PAGE0.EUL_HEAD_LB, 6, result);
		
		value = (result[3]<<8) | result[2];
		return value;		
	};

	public int GetEulerPitch() {
		byte[] result = new byte[6];
		int value;
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.read(REG_PAGE0.EUL_HEAD_LB, 6, result);
		
		value = (result[5]<<8) | result[4];
		return value;		
	};
	
	private I2C m_i2c;
	
	class MODES {
		static final int CONFIG = 0;
		static final int ACCONLY = 1;
		static final int MAG_ONLY = 2;
		static final int GYR_ONLY = 3;
		static final int ACC_MAG = 4;
		static final int ACC_GYR = 5;
		static final int MAG_GYR = 6;
		static final int A_M_G = 7;
		static final int IMU = 8;
		static final int COMPASS = 9;
		static final int M4G = 10;
		static final int NDOF_FMC_OFF = 11;
		static final int NDOF = 12;
	}
	
	//Most config is on PAGE 1
	class REG_PAGE1 {
		static final int PAGE_ID = 7;	//Write this to change PAGE (0,1)
		static final int ACC_SET = 8;	//Ah to have structs... 
		static final int MAG_SET = 9;
		static final int GYR_SET0  = 10;
		static final int GYR_SET1  = 11;
		static final int ACC_SLEEP = 12;
		static final int GYR_SLEEP = 13;
		static final int RSVD14 = 14;
		static final int INT_MASK = 15;
		static final int INT_ENABLE = 16;
		static final int ACC_AM_THRESH = 17;
		static final int ACC_INT_SET = 18;
		static final int ACC_HG_DURATION  = 19;
		static final int ACC_HG_THRESH = 20;
		static final int ACC_NM_THRESH = 21;
		static final int ACC_NM_SET = 22;
		static final int GYR_INT_SET = 23;
		static final int GYR_HR_X_SET = 24;
		static final int GYR_DUR_X = 25;
		static final int GYR_HR_Y_SET = 26;
		static final int GYR_DUR_Y = 27;
		static final int GYR_HR_Z_SET = 28;
		static final int GYR_DUR_Z = 29;
		static final int GYR_AM_THRESH = 30;
		static final int GYR_AM_SET = 31;
		
		static final int UNIQUE_ID = 0x50;	//Either 0x50 or 0x50-0x5F. Datasheet ambiguous
	}
	
	//Most of the Data Output is on Page 0
	class REG_PAGE0{
		static final int CHIP_ID = 0;
		static final int ACC_ID = 1;
		static final int MAG_ID = 2;
		static final int GYR_ID = 3;
		static final int SW_REV_LB = 4;
		static final int SW_REV_HB = 5;
		static final int BL_REV = 6;
		static final int PAGE_ID = 7;
		static final int ACC_X_LB = 8;
		static final int ACC_X_HB = 9;
		static final int ACC_Y_LB = 10;
		static final int ACC_Y_HB = 11;
		static final int ACC_Z_LB = 12;
		static final int ACC_Z_HB = 13;
		static final int MAG_X_LB = 14;
		static final int MAG_X_HB = 15;
		static final int MAG_Y_LB = 16;
		static final int MAG_Y_HB = 17;
		static final int MAG_Z_LB = 18;
		static final int MAG_Z_HB = 19;
		static final int GYR_X_LB = 20;
		static final int GYR_X_HB = 21;
		static final int GYR_Y_LB = 22;
		static final int GYR_Y_HB = 23;
		static final int GYR_Z_LB = 24;
		static final int GYR_Z_HB = 25;	
		static final int EUL_HEAD_LB = 26;
		static final int EUL_HEAD_HB = 27;
		static final int EUL_ROLL_LB = 28;
		static final int EUL_ROLL_HB = 29;
		static final int EUL_PITCH_LB = 30;
		static final int EUL_PITCH_HB = 31;
		static final int QUA_W_LB = 32;
		static final int QUA_W_HB = 33;
		static final int QUA_X_LB = 34;
		static final int QUA_X_HB = 35;
		static final int QUA_Y_LB = 36;
		static final int QUA_Y_HB = 37;
		static final int QUA_Z_LB = 38;
		static final int QUA_Z_HB = 39;		
		static final int LIA_X_LB = 40;
		static final int LIA_X_HB = 41;
		static final int LIA_Y_LB = 42;
		static final int LIA_Y_HB = 43;
		static final int LIA_Z_LB = 44;
		static final int LIA_Z_HB = 45;
		static final int GRV_X_LB = 46;
		static final int GRV_X_HB = 47;
		static final int GRV_Y_LB = 48;
		static final int GRV_Y_HB = 49;
		static final int GRV_Z_LB = 50;
		static final int GRV_Z_HB = 51;
		static final int TEMPERATURE = 52;
		static final int CALIB_STAT = 53;
		static final int ST_RESULT = 54;
		static final int INT_STAT = 55;
		static final int CLK_STAT = 56;
		static final int SYS_STATUS = 57;
		static final int SYS_ERROR = 58;
		static final int UNIT_SELECT = 59;
		static final int RSVD60 = 60;
		static final int SYS_MODE = 61;
		static final int POWER_MODE = 62;
		static final int SYS_TRIGGER = 63;
		static final int TEMP_SOURCE = 64;
		static final int AXIS_CONFIG = 65;
		static final int AXIS_SIGN = 66;
		//Yeah there's more
		static final int ACC_X_OFF_LB = 85;
		static final int ACC_X_OFF_HB = 86;
		static final int ACC_Y_OFF_LB = 87;
		static final int ACC_Y_OFF_HB = 88;
		static final int ACC_Z_OFF_LB = 89;
		static final int ACC_Z_OFF_HB = 90;
		static final int MAG_X_OFF_LB = 91;
		static final int MAG_X_OFF_HB = 92;
		static final int MAG_Y_OFF_LB = 93;
		static final int MAG_Y_OFF_HB = 94;	
		static final int MAG_Z_OFF_LB = 95;
		static final int MAG_Z_OFF_HB = 96;
		static final int GYR_X_OFF_LB = 97;
		static final int GYR_X_OFF_HB = 98;
		static final int GYR_Y_OFF_LB = 99;
		static final int GYR_Y_OFF_HB = 100;		
		static final int GYR_Z_OFF_LB = 101;
		static final int GYR_Z_OFF_HB = 102;
		static final int ACC_RAD_LB = 103;
		static final int ACC_RAD_HB = 104;
		static final int MAG_RAD_LB = 105;
		static final int MAG_RAD_HB = 106;
	}
	//public MODES m_modes;
	//public REG_PAGE0 m_page0;
	//public REG_PAGE1 m_page1;
}