package sneer.bricks.snapps.system.meter.bandwidth.gui.impl;

import static basis.environments.Environments.my;

import java.awt.Container;

import javax.swing.Icon;
import javax.swing.JLabel;

import basis.lang.Functor;

import sneer.bricks.pulp.bandwidth.BandwidthCounter;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.bricks.skin.main.dashboard.InstrumentPanel;
import sneer.bricks.skin.main.icons.Icons;
import sneer.bricks.skin.main.instrumentregistry.InstrumentRegistry;
import sneer.bricks.skin.widgets.reactive.ReactiveWidgetFactory;
import sneer.bricks.skin.widgets.reactive.TextWidget;
import sneer.bricks.snapps.system.meter.bandwidth.gui.BandwidthMeterGui;

class BandwidthMeterGuiImpl implements BandwidthMeterGui {


	{
		my(InstrumentRegistry.class).registerInstrument(this);
	} 
	
	
	private class MaxHolderFunctor implements Functor<Integer, String>{
		int _maxValue = 0;
		@Override public String evaluate(Integer value) {
			if(_maxValue<value) _maxValue=value;
			return value + " (" + _maxValue + ")";
		}
	}
	
	
	@Override
	public void init(InstrumentPanel window) {
		JLabel _bpsPeakLabel = new JLabel("kB/s(Peak)");
		TextWidget<JLabel> _uploadText = newLabel(my(BandwidthCounter.class).uploadSpeedInKBperSecond());
		TextWidget<JLabel> _downloadText = newLabel(my(BandwidthCounter.class).downloadSpeedInKBperSecond());
		JLabel _uploadIcon = newIcon("upload.png"); 
		JLabel _downloadIcon = newIcon("download.png");

		Container container = window.contentPane();
		container.add(_bpsPeakLabel);
		container.add(_uploadIcon);
		container.add(_uploadText.getComponent());
		container.add(_downloadIcon);
		container.add(_downloadText.getComponent());
	}
	
	
	private TextWidget<JLabel> newLabel(final Signal<Integer> source){
		return my(ReactiveWidgetFactory.class).newLabel(maxHolder(source));
	}

	
	private Signal<String> maxHolder(Signal<Integer> input) {
		return my(Signals.class).adapt(input, new MaxHolderFunctor());
	}
	
	private JLabel newIcon(final String imageName){
		Icon icon = my(Icons.class).load(this.getClass(), imageName);
		return new JLabel(icon);
	}
	

	@Override
	public int defaultHeight() {
		return DEFAULT_HEIGHT;
	}

	
	@Override
	public String title() {
		return "Bandwith Meter";
	}

}