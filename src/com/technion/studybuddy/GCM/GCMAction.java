package com.technion.studybuddy.GCM;

public enum GCMAction
{

	ACK {
		@Override
		public String toString()
		{
			return "ACK";
		}
	},

	UPDATE {
		@Override
		public String toString()
		{
			return "update";
		}
	},
	DELETE {
		@Override
		public String toString()
		{
			return "delete";
		}
	}
}
