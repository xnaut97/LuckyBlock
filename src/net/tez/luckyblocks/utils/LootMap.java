package net.tez.luckyblocks.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Một loại {@link Map} na ná như {@link NavigableMap}. Tuy nhiên, nếu như
 * {@link NavigableMap} không thể chứa các cặp {@code K} giống nhau thì
 * {@link LootMap} lại có thể thực hiện điều đó.<br>
 * Tuy nhiên, bởi vì tính chất là có thể chứa các cặp key giống nhau, do đó, nếu
 * truy vấn một giá trị {@code V} bằng một giá trị {@code K} nào đó, mà trong
 * danh sách {@code KeyEntry} lại có nhiều giá trị {@code K} trùng lặp, thì sẽ
 * trả về ngẫu nhiên một trong các {@link LootEntry} khớp bởi giá trị {@code K}.
 */
public class LootMap<K extends Number, V> {

	/**
	 * Một list chứa toàn bộ các entries của map.<br>
	 */
	private List<LootEntry> entries = new ArrayList<>();

	/**
	 * Trả về giá trị {@code value} tương ứng với giá trị {@code key}.<br>
	 * Nếu giá trị {@code value} trả về bị {@code null} (Do không tồn tại key trong
	 * map hay bất cứ lí do gì), sẽ trả về giá trị {@code defaultValue} cho trước.
	 * 
	 * @param key
	 *            Key tương ứng với Value cần lấy
	 * @param defaultValue
	 *            Giá trị mặc định trả về nếu Value lấy ra bị null.
	 */
	public V getOrDefault(K key, V defaultValue) {
		V value = get(key);
		return value == null ? defaultValue : value;
	}

	/**
	 * Trả về một {@link Set} chứa toàn bộ các giá trị key.<br>
	 * Do trả về {@link Set} nên các giá trị trùng nhau sẽ chỉ gộp lại thành 1.
	 * 
	 * @return Tất cả các key.
	 */
	public Set<K> keySet() {
		Set<K> set = new HashSet<>();
		for (LootEntry entri : entries) {
			set.add(entri.getKey());
		}
		return set;
	}

	/**
	 * Trả về một {@link Collection} chứa toàn bộ các giá trị value.
	 */
	public Collection<V> values() {
		Collection<V> collect = new ArrayList<>();
		for (LootEntry entri : entries) {
			collect.add(entri.getValue());
		}
		return collect;
	}

	/**
	 * Trả về kích cỡ/lượng entry hiện có của map.
	 */
	public int size() {
		return entries.size();
	}

	/**
	 * Trả về giá trị {@code V} tương ứng với giá trị {@code key}.<br>
	 * Do bản chất của {@link LootMap} có thể chứa nhiều key trùng lặp cùng một lúc,
	 * do đó, nếu có nhiều giá trị {@code key}, hệ thống sẽ trả về giá trị
	 * {@code value} của một key ngẫu nhiên trùng {@code key}.
	 * 
	 * @param key
	 *            Key của giá trị cần lấy
	 * @return Giá trị tương ứng với key.
	 */
	public V get(K key) {
		if (!containsKey(key))
			return null;

		List<V> entri = new ArrayList<>();
		for (LootEntry en : entries) {
			if (en.getKey().equals(key)) {
				entri.add(en.getValue());
			}
		}

		return entri.get(ThreadLocalRandom.current().nextInt(entri.size()));
	}

	/**
	 * Trả về một giá trị nào đó gần bằng/gần giống dưới hoặc giống hệt giá trị
	 * {@code value} dựa trên việc so sánh. Với điều kiện giá trị
	 * {@link LootEntry#key} phải thuộc tập {@link Number}, nếu không sẽ ném ra
	 * {@link ClassCastException}.
	 * 
	 * @param value
	 *            Giá trị mẫu được đem ra so sánh
	 * @return Giá trị gần bằng/gần giống dưới hoặc giống hệt giá trị đó.
	 */
	public K floorKey(K value) {
		if (!(value instanceof Number))
			throw new ClassCastException(
					"Cannot cast " + value.getClass().toGenericString() + " to " + Number.class.toGenericString());

		if (size() == 0) {
			throw new ArrayIndexOutOfBoundsException("The index " + value + " is out of bounds.");
		}

		NavigableSet<K> values = new TreeSet<>(keySet());
		K val = values.floor(value);

		return val;
	}

	/**
	 * Trả về một giá trị nào đó gần bằng/gần giống trên hoặc giống hệt giá trị
	 * {@code value} dựa trên việc so sánh. Với điều kiện giá trị
	 * {@link LootEntry#key} phải thuộc tập {@link Number}, nếu không sẽ ném ra
	 * {@link ClassCastException}.
	 * 
	 * @param value
	 *            Giá trị mẫu được đem ra so sánh
	 * @return Giá trị gần bằng/gần giống trên hoặc giống hệt giá trị đó.
	 */
	public K ceilingKey(K value) {
		if (!(value instanceof Number))
			throw new ClassCastException(
					"Cannot cast " + value.getClass().toGenericString() + " to " + Number.class.toGenericString());

		if (size() == 0) {
			throw new ArrayIndexOutOfBoundsException("The index " + value + " is out of bounds.");
		}

		NavigableSet<K> values = new TreeSet<>(keySet());
		K val = values.ceiling(value);
		if(val == null) {
			return null;
		}
		return val;
	}
	
	public K getFirst()
	{
		NavigableSet<K> values = new TreeSet<>(keySet());
		K val = values.first();
		return val;
	}
	/**
	 * Thêm một cặp {@code [key, value]} vào map.
	 */
	public void put(K key, V value) {
		LootEntry entry = new LootEntry(key, value);
		entries.add(entry);
	}

	/**
	 * Xóa bỏ một phần tử nằm ở vị trí xác định.<br>
	 * Ném ra ngoại lệ {@link ArrayIndexOutOfBoundsException} nếu giá trị
	 * {@code index} lớn hơn kích cỡ của map.
	 * 
	 * @param index
	 *            Vị trí của phần tử cần xóa
	 */
	public void remove(int index) {
		entries.remove(index);
	}

	/**
	 * Dọn sạch toàn bộ phần tử trong map. Map trở nên trống.
	 */
	public void clear() {
		entries.clear();
	}

	/**
	 * Xóa tất cả các phần tử có trong map có key trùng với {@code obj}.
	 * 
	 * @param obj
	 *            Phần tử mẫu cần xóa
	 */
	public void remove(K obj) {
		if (!containsKey(obj))
			return;
		for (int i = 0; i < size(); i++) {
			LootEntry entry = entries.get(i);
			if (entry.getKey().equals(obj))
				entries.remove(entry);
		}
	}

	/**
	 * Kiểm tra xem trong map đã tồn tại một giá trị {@code key} nào chưa.
	 * 
	 * @param key
	 *            Giá trị cần kiểm tra
	 * @return {@code true} nếu có tồn tại trước đó, {@code false} trong trường hợp
	 *         ngược lại.
	 */
	public boolean containsKey(K key) {
		if (size() == 0)
			return false;
		for (LootEntry entry : entries) {
			if (entry.getKey().equals(key))
				return true;
		}
		return false;
	}

	/**
	 * Kiểm tra xem trong map đã tồn tại một giá trị {@code value} nào chưa.
	 * 
	 * @param value
	 *            Giá trị cần kiểm tra
	 * @return {@code true} nếu có tồn tại trước đó, {@code false} trong trường hợp
	 *         ngược lại.
	 */
	public boolean containsValue(V value) {
		if (size() == 0)
			return false;
		for (LootEntry entry : entries) {
			if (entry.getValue().equals(value))
				return true;
		}
		return false;
	}
	
	/**
	 * Phần tử của {@link LootMap}.
	 */
	private class LootEntry {

		private K key;
		private V value;

		public LootEntry(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}

}
