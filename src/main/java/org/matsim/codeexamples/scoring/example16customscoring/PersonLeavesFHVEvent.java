package org.matsim.codeexamples.scoring.example16customscoring;

import java.util.Map;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.api.internal.HasPersonId;

public class PersonLeavesFHVEvent extends Event implements HasPersonId {

	private Id<Person> personId;
	
	public PersonLeavesFHVEvent(double time, Id<Person> personId) {
		super(time);
		this.personId = personId;
	}

	@Override
	public Id<Person> getPersonId() {
		return personId;
	}

	@Override
	public String getEventType() {
		return "FHV";
	}

//	@Override
//	public Map<String, String> getAttributes() {
//		final Map<String, String> attributes = super.getAttributes();
//		attributes.put("person", getPersonId().toString());
//		return attributes;
//	}
}
