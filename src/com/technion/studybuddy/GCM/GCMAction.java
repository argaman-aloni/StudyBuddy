package com.technion.studybuddy.GCM;

public enum GCMAction
{

	ACK
	{
		public String toString()
		{
			return "ACK";
		}
	},

	UPDATE
	{
		public String toString()
		{
			return "update";
		}
	}
}
