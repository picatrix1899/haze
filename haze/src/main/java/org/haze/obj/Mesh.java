package org.haze.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Mesh
{
	public String name;
	public Optional<String> material = Optional.empty();
	public List<Face> faces = new ArrayList<>();
}
