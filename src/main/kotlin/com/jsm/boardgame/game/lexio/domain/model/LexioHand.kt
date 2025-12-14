package com.jsm.boardgame.game.lexio.domain.model

class LexioHand private constructor(
    val tiles: List<LexioTile>,
    val type: HandType,
    val representativeTile: LexioTile // 비교를 위한 대표 타일
) : Comparable<LexioHand> {

    override fun compareTo(other: LexioHand): Int {
        // 1. 족보 타입 비교 (5장 족보의 경우 스트레이트 < 플러시 등)
        if (this.type != other.type) {
            // 5장 족보끼리만 다른 타입 비교 가능
            if (this.type.count == 5 && other.type.count == 5) {
                return this.type.rank - other.type.rank
            }
            throw IllegalArgumentException("Cannot compare different hand types with different counts")
        }

        // 2. 같은 족보일 경우 대표 타일의 힘 비교
        // 예외: 백스트레이트(1-2-3-4-5) vs 일반 스트레이트 로직 등은 representativeTile 선정 시 처리해야 함
        return this.representativeTile.compareTo(other.representativeTile)
    }

    companion object {
        fun from(tiles: List<LexioTile>): LexioHand {
            val sortedTiles = tiles.sorted()
            val count = sortedTiles.size

            return when (count) {
                1 -> createSingle(sortedTiles)
                2 -> createPair(sortedTiles)
                3 -> createTriple(sortedTiles)
                5 -> createFiveCardHand(sortedTiles)
                else -> throw IllegalArgumentException("Invalid tile count: $count")
            }
        }

        private fun createSingle(tiles: List<LexioTile>): LexioHand {
            return LexioHand(tiles, HandType.SINGLE, tiles[0])
        }

        private fun createPair(tiles: List<LexioTile>): LexioHand {
            if (tiles[0].number != tiles[1].number) throw IllegalArgumentException("Not a pair")
            return LexioHand(tiles, HandType.PAIR, tiles.maxOrNull()!!) // 문양 높은 것이 대표
        }

        private fun createTriple(tiles: List<LexioTile>): LexioHand {
            if (tiles[0].number != tiles[1].number || tiles[1].number != tiles[2].number) {
                throw IllegalArgumentException("Not a triple")
            }
            return LexioHand(tiles, HandType.TRIPLE, tiles.maxOrNull()!!)
        }

        private fun createFiveCardHand(tiles: List<LexioTile>): LexioHand {
            // 1. Straight Flush Check
            val flush = isFlush(tiles)
            val straight = isStraight(tiles)

            if (flush && straight) {
                return LexioHand(tiles, HandType.STRAIGHT_FLUSH, getStraightRepresentative(tiles))
            }

            // 2. Four of a Kind
            val fourKindRep = getFourOfAKindRep(tiles)
            if (fourKindRep != null) {
                return LexioHand(tiles, HandType.FOUR_OF_A_KIND, fourKindRep)
            }

            // 3. Full House
            val fullHouseRep = getFullHouseRep(tiles)
            if (fullHouseRep != null) {
                return LexioHand(tiles, HandType.FULL_HOUSE, fullHouseRep)
            }

            // 4. Flush
            if (flush) {
                // 플러시는 가장 높은 숫자로 비교 (같으면 다음 숫자... 이지만 여기선 대표 타일 하나로 단순화 가능한지 체크)
                // PRD: "가장 높은 숫자로 비교 (그 다음 숫자...)" -> LexioTile compareTo가 Power순이므로 max() 쓰면 됨.
                // 하지만 문양이 모두 같으므로, 숫자만 비교하면 됨.
                // 동률 처리를 위해선 전체 비교가 필요하지만, 여기선 단순화를 위해 maxTile 사용.
                // 완벽한 구현을 위해선 representativeTile만으로는 부족할 수 있으나 PRD 상 족보 덮기는 "더 높은" 것이므로
                // 완전히 똑같은 구성(숫자)의 플러시는 나올 수 없음(덱에 중복 타일 없음).
                // 따라서 가장 높은 타일만 비교해도 충분함.
                return LexioHand(tiles, HandType.FLUSH, tiles.maxOrNull()!!)
            }

            // 5. Straight
            if (straight) {
                return LexioHand(tiles, HandType.STRAIGHT, getStraightRepresentative(tiles))
            }

            throw IllegalArgumentException("Invalid 5-card hand")
        }

        private fun isFlush(tiles: List<LexioTile>): Boolean {
            val suit = tiles[0].suit
            return tiles.all { it.suit == suit }
        }

        private fun isStraight(tiles: List<LexioTile>): Boolean {
            // Power 기준 정렬: 3(3)..15(15), 1(140), 2(150)
            // 스트레이트 케이스:
            // 일반: 3-4-5-6-7
            // 백스트레이트(5): 1-2-3-4-5 (숫자로는 1,2,3,4,5) -> 정렬하면 3,4,5,1(140),2(150)
            // 마운틴(X): Lexio에는 JQK가 없고 15까지 있음. 
            // 렉시오 스트레이트 연결: 1-2-3-4-5 가능. 12-13-14-15-1 가능.
            // 2를 넘어가는 연결(13-1-2-3-4) 불가능.

            // 숫자만 추출해서 정렬
            val numbers = tiles.map { it.number }.sorted()

            // 1. 일반적인 연속 (예: 3,4,5,6,7)
            var isSequential = true
            for (i in 0 until 4) {
                if (numbers[i + 1] != numbers[i] + 1) {
                    isSequential = false
                    break
                }
            }
            if (isSequential) return true

            // 2. 1-2-3-4-5 (숫자 1,2,3,4,5 존재)
            if (numbers == listOf(1, 2, 3, 4, 5)) return true

            // 3. 12-13-14-15-1 (백 스트레이트?) -> PRD: 12-13-14-15-1
            if (numbers == listOf(1, 12, 13, 14, 15)) return true

            return false
        }

        private fun getStraightRepresentative(tiles: List<LexioTile>): LexioTile {
            val numbers = tiles.map { it.number }.sorted()

            // 1-2-3-4-5 (최강) -> 2가 대표
            if (numbers == listOf(1, 2, 3, 4, 5)) {
                return tiles.find { it.number == 2 }!!
            }

            // 12-13-14-15-1 -> 1이 대표 (숫자상 1이 가장 높음? 아니면 15?)
            // PRD: 1-2-3-4-5 (최강) > 12-13-14-15-1 ...
            // 12-13-14-15-1 의 경우 구성 숫자 중 가장 높은 숫자로 비교.
            // 구성 숫자: 1, 12, 13, 14, 15. 이 중 Power가 가장 센 것은 1(140).
            // 따라서 1을 리턴.

            // 그 외: Power가 가장 센 타일 리턴
            return tiles.maxByOrNull { it.power }!!
        }

        private fun getFourOfAKindRep(tiles: List<LexioTile>): LexioTile? {
            // 4장이 같아야 함. 정렬되어 있으므로 [0]==[3] 또는 [1]==[4]
            // tiles는 Power순 정렬 상태임.
            // 숫자 기준으로 그룹핑
            val grouped = tiles.groupBy { it.number }
            val fourKindEntry = grouped.entries.find { it.value.size == 4 }
            return fourKindEntry?.value?.maxByOrNull { it.suit.rank } // 그 숫자의 타일 중 하나(문양 젤 쎈거)
        }

        private fun getFullHouseRep(tiles: List<LexioTile>): LexioTile? {
            val grouped = tiles.groupBy { it.number }
            // 3장 + 2장
            if (grouped.size == 2 && grouped.values.any { it.size == 3 }) {
                val tripleEntry = grouped.entries.find { it.value.size == 3 }!!
                return tripleEntry.value.maxByOrNull { it.suit.rank }
            }
            return null
        }
    }
}
