package org.haze.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Model
{
	public Optional<String> materialList = Optional.empty();
	public List<Mesh> meshes = new ArrayList<>();
}
