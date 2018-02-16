package org.usfirst.frc.team1444.robot.controlling;

import com.mach.LightDrive.Color;
import edu.wpi.first.wpilibj.I2C;

import java.nio.*;

public class BNO055_IMU {

	public BNO055_IMU() {
		I2C m_i2c = new I2C(I2C.kOnboard, 0x28);
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
		return (result>0x3F);
	};
	
	public void SetMode(MODES mode) {
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.write(REG_PAGE0.SYS_MODE, mode);
	};
	
	public int GetEulerHeading() {
		byte[] result = {0,0,0,0,0,0};
		int value;
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.read(REG_PAGE0.EUL_HEAD_LB, 6, result);
		
		value = (result[1]<<8) | result[0];
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
		byte[] result = {0,0,0,0,0,0};
		int value;
		m_i2c.write(REG_PAGE0.PAGE_ID, 0);
		m_i2c.read(REG_PAGE0.EUL_HEAD_LB, 6, result);
		
		value = (result[5]<<8) | result[4];
		return value;		
	};
	
	private I2C m_i2c;
	
	enum MODES {
		CONFIG		0,
		ACCONLY		1,
		MAG_ONLY	2,
		GYR_ONLY	3,
		ACC_MAG		4,
		ACC_GYR		5,
		MAG_GYR		6,
		A_M_G		7,
		IMU			8,
		COMPASS		9,
		M4G			10,
		NDOF_FMC_OFF	11,
		NDOF		12
	};
	
	//Most config is on PAGE 1
	enum REG_PAGE1 {
		PAGE_ID			7,	//Write this to change PAGE (0,1)
		ACC_SET			8,	//Ah to have structs... 
		MAG_SET			9,
		GYR_SET0 		10,
		GYR_SET1 		11,
		ACC_SLEEP		12,
		GYR_SLEEP		13,
		RSVD14			14,
		INT_MASK		15,
		INT_ENABLE		16,
		ACC_AM_THRESH	17,
		ACC_INT_SET		18,
		ACC_HG_DURATION 19,
		ACC_HG_THRESH	20,
		ACC_NM_THRESH	21,
		ACC_NM_SET		22,
		GYR_INT_SET		23,
		GYR_HR_X_SET	24,
		GYR_DUR_X		25,
		GYR_HR_Y_SET	26,
		GYR_DUR_Y		27,
		GYR_HR_Z_SET	28,
		GYR_DUR_Z		29,
		GYR_AM_THRESH	30,
		GYR_AM_SET		31,
		
		UNIQUE_ID		0x50	//Either 0x50 or 0x50-0x5F. Datasheet ambiguous
	};
	
	//Most of the Data Output is on Page 0
	enum REG_PAGE0 {
		CHIP_ID			0,
		ACC_ID			1,
		MAG_ID			2,
		GYR_ID			3,
		SW_REV_LB		4,
		SW_REV_HB		5,
		BL_REV			6,
		PAGE_ID			7,
		ACC_X_LB		8,
		ACC_X_HB		9,
		ACC_Y_LB		10,
		ACC_Y_HB		11,
		ACC_Z_LB		12,
		ACC_Z_HB		13,
		MAG_X_LB		14,
		MAG_X_HB		15,
		MAG_Y_LB		16,
		MAG_Y_HB		17,
		MAG_Z_LB		18,
		MAG_Z_HB		19,
		GYR_X_LB		20,
		GYR_X_HB		21,
		GYR_Y_LB		22,
		GYR_Y_HB		23,
		GYR_Z_LB		24,
		GYR_Z_HB		25,	
		EUL_HEAD_LB		26,
		EUL_HEAD_HB		27,
		EUL_ROLL_LB		28,
		EUL_ROLL_HB		29,
		EUL_PITCH_LB	30,
		EUL_PITCH_HB	31,
		QUA_W_LB		32,
		QUA_W_HB		33,
		QUA_X_LB		34,
		QUA_X_HB		35,
		QUA_Y_LB		36,
		QUA_Y_HB		37,
		QUA_Z_LB		38,
		QUA_Z_HB		39,		
		LIA_X_LB		40,
		LIA_X_HB		41,
		LIA_Y_LB		42,
		LIA_Y_HB		43,
		LIA_Z_LB		44,
		LIA_Z_HB		45,
		GRV_X_LB		46,
		GRV_X_HB		47,
		GRV_Y_LB		48,
		GRV_Y_HB		49,
		GRV_Z_LB		50,
		GRV_Z_HB		51,
		TEMPERATURE		52,
		CALIB_STAT		53,
		ST_RESULT		54,
		INT_STAT		55,
		CLK_STAT		56,
		SYS_STATUS		57,
		SYS_ERROR		58,
		UNIT_SELECT		59,
		RSVD60			60,
		SYS_MODE		61,
		POWER_MODE		62,
		SYS_TRIGGER		63,
		TEMP_SOURCE		64,
		AXIS_CONFIG		65,
		AXIS_SIGN		66,
		//Yeah there's more
		ACC_X_OFF_LB	85,
		ACC_X_OFF_HB	86,
		ACC_Y_OFF_LB	87,
		ACC_Y_OFF_HB	88,
		ACC_Z_OFF_LB	89,
		ACC_Z_OFF_HB	90,
		MAG_X_OFF_LB	91,
		MAG_X_OFF_HB	92,
		MAG_Y_OFF_LB	93,
		MAG_Y_OFF_HB	94,	
		MAG_Z_OFF_LB	95,
		MAG_Z_OFF_HB	96,
		GYR_X_OFF_LB	97,
		GYR_X_OFF_HB	98,
		GYR_Y_OFF_LB	99,
		GYR_Y_OFF_HB	100,		
		GYR_Z_OFF_LB	101,
		GYR_Z_OFF_HB	102,
		ACC_RAD_LB		103,
		ACC_RAD_HB		104,
		MAG_RAD_LB		105,
		MAG_RAD_HB		106
	};
	
}