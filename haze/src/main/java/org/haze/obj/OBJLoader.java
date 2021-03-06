package org.haze.obj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java.util.ArrayList;
import java.util.List;

import org.barghos.math.vector.vec2.Vec2f;
import org.barghos.math.vector.vec3.Vec3f;

public class OBJLoader
{
	private static final String LK_MATLIB = "mtllib";
	
	private static final String LK_NEW_MESH = "o";
	
	private static final String LK_VERTEX = "v";
	private static final String LK_NORMAL = "vn";
	private static final String LK_TEXCOORD = "vt";
	
	private static final String LK_USE_MAT = "usemtl";
	
	private static final String LK_FACE = "f";
	
	public Model read(String path) throws IOException
	{
		Reader reader = new FileReader(path);
		
		Model info = read(reader);
		
		reader.close();
		
		return info;
	}
	
	public Model read(File file) throws IOException
	{
		Reader reader = new FileReader(file);
		
		Model info = read(reader);
		
		reader.close();
		
		return info;
	}
	
	public Model read(Reader inputReader) throws IOException
	{
		BufferedReader reader = new BufferedReader(inputReader);

		ModelRaw model = new ModelRaw();
		MeshRaw mesh = null;
		
		List<Vec3f> vertices = new ArrayList<>();
		List<Vec3f> normals = new ArrayList<>();
		List<Vec2f> uvs = new ArrayList<>();

		String line = "";
		while((line = reader.readLine()) != null)
		{
			line = line.trim();
			
			if(line.startsWith(LK_MATLIB + " "))
			{
				line = line.replaceFirst(LK_MATLIB, "").trim();
				
				model.materialList = line;
				
				continue;
			}
			
			if(line.startsWith(LK_NEW_MESH + " "))
			{
				line = line.replaceFirst(LK_NEW_MESH, "").trim();
				
				mesh = new MeshRaw();
				mesh.name = line;
				model.meshes.add(mesh);
				
				continue;
			}
			
			if(line.startsWith(LK_VERTEX + " "))
			{
				line = line.replaceFirst(LK_VERTEX, "").trim();
				String[] parts = line.split(" ");
				vertices.add(new Vec3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
				
				continue;
			}
			
			if(line.startsWith(LK_NORMAL + " "))
			{
				line = line.replaceFirst(LK_NORMAL, "").trim();
				String[] parts = line.split(" ");
				
				normals.add(new Vec3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
				
				continue;
			}
			
			if(line.startsWith(LK_TEXCOORD + " "))
			{
				line = line.replaceFirst(LK_TEXCOORD, "").trim();
				String[] parts = line.split(" ");
				
				uvs.add(new Vec2f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1])));
				
				continue;
			}
			
			if(line.startsWith(LK_USE_MAT + " "))
			{
				line = line.replaceFirst(LK_USE_MAT, "").trim();
				
				mesh.material = line;
				
				continue;
			}
			
			if(line.startsWith(LK_FACE + " "))
			{
				line = line.replaceFirst(LK_FACE, "").trim();
				String[] parts = line.split(" ");
				String[] vertexParts;
				
				FaceRaw face = new FaceRaw();
				vertexParts = parts[0].split("/");
				face.vertexA = new VertexRaw();
				face.vertexA.position = vertices.get(Integer.parseInt(vertexParts[0]) - 1);
				face.vertexA.uv = uvs.get(Integer.parseInt(vertexParts[1]) - 1);
				face.vertexA.normal = normals.get(Integer.parseInt(vertexParts[2]) - 1);
				vertexParts = parts[1].split("/");
				face.vertexB = new VertexRaw();
				face.vertexB.position = vertices.get(Integer.parseInt(vertexParts[0]) - 1);
				face.vertexB.uv = uvs.get(Integer.parseInt(vertexParts[1]) - 1);
				face.vertexB.normal = normals.get(Integer.parseInt(vertexParts[2]) - 1);
				vertexParts = parts[2].split("/");
				face.vertexC = new VertexRaw();
				face.vertexC.position = vertices.get(Integer.parseInt(vertexParts[0]) - 1);
				face.vertexC.uv = uvs.get(Integer.parseInt(vertexParts[1]) - 1);
				face.vertexC.normal = normals.get(Integer.parseInt(vertexParts[2]) - 1);
				
				mesh.faces.add(face);
				
				continue;
			}
		}
		
		Model out = process(model);
		
		return out;
	}
	
	private Model process(ModelRaw list) throws IOException
	{
		Model out = new Model();
		
		for(MeshRaw rawMesh : list.meshes)
		{
			Mesh mesh = new Mesh();
			mesh.name = rawMesh.name;
			mesh.material = rawMesh.material;
			
			for(FaceRaw rawFace : rawMesh.faces)
			{
				Face face = new Face();
				face.vertexA = new Vertex();
				face.vertexA.position = rawFace.vertexA.position;
				face.vertexA.uv = rawFace.vertexA.uv;
				face.vertexA.normal = rawFace.vertexA.normal;
				face.vertexA.tangent = new Vec3f();
				face.vertexB = new Vertex();
				face.vertexB.position = rawFace.vertexB.position;
				face.vertexB.uv = rawFace.vertexB.uv;
				face.vertexB.normal = rawFace.vertexB.normal;
				face.vertexB.tangent = new Vec3f();
				face.vertexC = new Vertex();
				face.vertexC.position = rawFace.vertexC.position;
				face.vertexC.uv = rawFace.vertexC.uv;
				face.vertexC.normal = rawFace.vertexC.normal;
				face.vertexC.tangent = new Vec3f();
				
				processFace(face);
				
				mesh.faces.add(face);
			}
			
			out.meshes.add(mesh);
		}
		
		out.materialList = list.materialList;
		
		return out;
	}
	
	private void processFace(Face face)
	{
		Vec3f v1 = face.vertexB.position.subN(face.vertexA.position);
		Vec3f v2 = face.vertexC.position.subN(face.vertexA.position);
		
		Vec3f n = v1.cross(v2);
		
		if((n.dot(face.vertexA.normal) < 0.0f && n.dot(face.vertexB.normal) < 0.0f) ||
			(n.dot(face.vertexB.normal) < 0.0f && n.dot(face.vertexC.normal) < 0.0f) ||
			(n.dot(face.vertexA.normal) < 0.0f && n.dot(face.vertexC.normal) < 0.0f))
		{
			n = v2.cross(v1);
		}
		
		face.normal = n;
		
		calculateTangents(face.vertexA, face.vertexB, face.vertexC);
	}
	
	private void calculateTangents(Vertex a, Vertex b, Vertex c)
	{
		Vec3f deltaPos1 = b.position.subN(a.position);
		Vec3f deltaPos2 = c.position.subN(a.position);
		
		Vec2f uv0 = a.uv;
		Vec2f uv1 = b.uv;
		Vec2f uv2 = c.uv;
		
		Vec2f deltaUv1 = uv1.sub(uv0, new Vec2f());
		Vec2f deltaUv2 = uv2.sub(uv0, new Vec2f());

		float r = 1.0f / (deltaUv1.getX() * deltaUv2.getY() - deltaUv1.getY() * deltaUv2.getX());
		deltaPos1.mul(deltaUv2.getY());
		deltaPos2.mul(deltaUv1.getY());
		Vec3f tangent = deltaPos1.subN(deltaPos2);
		tangent.mul(r);
		
		a.tangent.add(tangent, a.tangent);
		b.tangent.add(tangent, b.tangent);
		c.tangent.add(tangent, c.tangent);
	}
}
