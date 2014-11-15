package me.anonim1133.testpedo;

public class Average {

	private short count = 0;
	private short limit = 0;
	private short sum = 0;

	public Average(short limit) {
		this.limit = limit;
	}

	public float add(short value){
		count++;

		this.sum += value;

		if(this.count == this.limit){
			float ret = (float)(sum/count);

			this.reset();

			return ret;
		}else return 0;
	}

	private void reset(){
		this.count = 0;
		this.sum = 0;
	}

}
